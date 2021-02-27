package xworker.org.apache.kafka.streams.processor;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ActionProcessorSupplier implements ProcessorSupplier<Object, Object>{
	Thing thing;
	ActionContext actionContext;
	
	public ActionProcessorSupplier(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}

	public static ActionProcessorSupplier create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new ActionProcessorSupplier(self, actionContext);
	}

	@Override
	public Processor<Object, Object> get() {
		return new ActionProcessor(thing, actionContext);
	}



}
