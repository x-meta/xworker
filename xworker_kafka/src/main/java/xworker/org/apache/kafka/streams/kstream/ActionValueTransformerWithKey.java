package xworker.org.apache.kafka.streams.kstream;

import org.apache.kafka.streams.kstream.ValueTransformerWithKey;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ActionValueTransformerWithKey  implements ValueTransformerWithKey<Object, Object, Object>{
	Thing thing;
	ActionContext actionContext;
	
	public ActionValueTransformerWithKey(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}

	public static ActionValueTransformerWithKey create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new ActionValueTransformerWithKey(self, actionContext);
	}

	@Override
	public void init(ProcessorContext context) {
		thing.doAction("init", actionContext, "context", context, "valueTransformer", this);
	}

	@Override
	public Object transform(Object key, Object value) {
		return thing.getAction().run(actionContext, "key", key, "value", value, "valueTransformer", this);
	}

	@Override
	public void close() {
		thing.doAction("close", actionContext, "valueTransformer", this);
	}

}
