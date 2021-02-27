package xworker.org.apache.kafka.streams.kstream;

import org.apache.kafka.streams.kstream.ForeachAction;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingForeachAction implements  ForeachAction<Object, Object>{
	Thing thing;
	ActionContext actionContext;
	
	public ThingForeachAction(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}

	public static ThingForeachAction create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new ThingForeachAction(self, actionContext);
	}

	@Override
	public void apply(Object key, Object value) {
		thing.doAction("apply", actionContext, "key", key, "value", value);
	}

}
