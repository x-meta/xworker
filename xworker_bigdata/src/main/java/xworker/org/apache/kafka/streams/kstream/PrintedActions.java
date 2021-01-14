package xworker.org.apache.kafka.streams.kstream;

import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Printed;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class PrintedActions {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Printed create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String fileName = self.doAction("getFileName", actionContext);
		String labelName = self.doAction("getLabelName", actionContext);		
		String processorName = self.doAction("getProcessorName", actionContext);
		
		Printed<?,?> p = null;
		if(fileName != null && !"".equals(fileName)) {
			p = Printed.toFile(fileName);
		}else {
			p = Printed.toSysOut();
		}
		
		if(labelName != null) {
			p.withLabel(labelName);
		}
		
		if(processorName != null) {
			p.withName(processorName);
		}
		
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof KeyValueMapper) {
				p.withKeyValueMapper((KeyValueMapper) obj);
				break;
			}
		}
		
		return p;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void build(ActionContext actionContext) {
		KStream stream = actionContext.getObject("kstream");
		Printed printed = create(actionContext);
		stream.print(printed);
	}
}
