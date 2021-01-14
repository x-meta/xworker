package xworker.org.apache.kafka.streams.kstream;

import org.apache.kafka.streams.kstream.ValueJoiner;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingValueJoiner implements ValueJoiner<Object, Object, Object>{
	Thing thing;
	ActionContext actionContext;
	
	public ThingValueJoiner(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}

	public static ThingValueJoiner create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ThingValueJoiner joiner = new ThingValueJoiner(self, actionContext);
		actionContext.l().put(self.getMetadata().getName(), joiner);
		
		return joiner;
	}

	@Override
	public Object apply(Object key, Object value) {
		return thing.doAction("apply", actionContext, "key", key, "value", value);
	}

}
