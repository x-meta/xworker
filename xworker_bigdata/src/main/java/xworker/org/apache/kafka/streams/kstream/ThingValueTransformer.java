package xworker.org.apache.kafka.streams.kstream;

import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingValueTransformer  implements ValueTransformer<Object, Object>{
	Thing thing;
	ActionContext actionContext;
	
	public ThingValueTransformer(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}

	public static ThingValueTransformer create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new ThingValueTransformer(self, actionContext);
	}

	@Override
	public void init(ProcessorContext context) {
		thing.doAction("init", actionContext, "valueTransformer", this);
	}

	@Override
	public Object transform(Object value) {
		return thing.doAction("transform", actionContext, "value", value, "valueTransformer", this);
	}

	@Override
	public void close() {
		thing.doAction("close", actionContext, "valueTransformer", this);		
	}
}
