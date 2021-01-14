package xworker.org.apache.kafka.streams.kstream;

import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ActionTransformer  implements Transformer<Object, Object, Object>{
	Thing thing;
	ActionContext actionContext;
	
	public ActionTransformer(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}

	public static ActionTransformer create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new ActionTransformer(self, actionContext);
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
