package xworker.org.apache.kafka.streams.kstream;

import org.apache.kafka.streams.kstream.ValueJoiner;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ActionValueJoiner implements ValueJoiner<Object, Object, Object>{
	Thing thing;
	ActionContext actionContext;
	
	public ActionValueJoiner(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	@Override
	public Object apply(Object key, Object value) {
		return thing.getAction().run(actionContext, "key", key, "value", value);
	}

	public static ActionValueJoiner create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ActionValueJoiner joiner = new ActionValueJoiner(self, actionContext);
		actionContext.l().put(self.getMetadata().getName(), joiner);
		
		return joiner;
	}


}
