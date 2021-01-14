package xworker.org.apache.kafka.streams.kstream;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.processor.StreamPartitioner;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ProducedActions {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Produced produced = null;
		
		String processorName = self.doAction("getProcessorName", actionContext);
		if(processorName != null) {
			produced = Produced.as(processorName);
		}
		
		Serde<?> keySerde = self.doAction("getKeySerde", actionContext);
		Serde<?> valueSerde = self.doAction("getValueSerde", actionContext);
		if(produced == null) {
			if(keySerde != null && valueSerde != null) {
				produced = Produced.with(keySerde, valueSerde);
			}
		}else {
			if(keySerde != null) {
				produced.withKeySerde(keySerde);
			}
			
			if(valueSerde != null) {
				produced.withValueSerde(valueSerde);
			}
		}
		
		for(Thing child : self.getChilds()) {
			Object obj = child.doAction("create", actionContext);
			if(obj instanceof StreamPartitioner) {
				if(produced != null) {
					produced.withStreamPartitioner((StreamPartitioner) obj);
				}else {
					produced = Produced.streamPartitioner((StreamPartitioner) obj);
				}
			}
		}		
		
		actionContext.l().put(self.getMetadata().getName(), produced);
		return produced;
	}
}
