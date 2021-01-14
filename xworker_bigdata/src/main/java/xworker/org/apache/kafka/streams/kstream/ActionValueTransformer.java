package xworker.org.apache.kafka.streams.kstream;

import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ActionValueTransformer implements ValueTransformer<Object, Object>{
	Thing thing;
	ActionContext actionContext;
	
	public ActionValueTransformer(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}

	public static ActionValueTransformer create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new ActionValueTransformer(self, actionContext);
	}

	@Override
	public void init(ProcessorContext context) {
		thing.doAction("init", actionContext, "context", context, "valueTransformer", this);
	}

	@Override
	public Object transform(Object value) {
		return thing.getAction().run(actionContext, "value", value, "valueTransformer", this);
	}

	@Override
	public void close() {
		thing.doAction("close", actionContext, "valueTransformer", this);
	}


}
