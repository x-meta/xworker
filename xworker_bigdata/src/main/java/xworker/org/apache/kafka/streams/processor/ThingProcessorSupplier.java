package xworker.org.apache.kafka.streams.processor;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingProcessorSupplier  implements ProcessorSupplier<Object, Object>{
	Thing thing;
	ActionContext actionContext;
	
	public ThingProcessorSupplier(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}

	public static ThingProcessorSupplier create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new ThingProcessorSupplier(self, actionContext);
	}

	@Override
	public Processor<Object, Object> get() {
		return new ThingProcessor(thing, actionContext);
	}



}
