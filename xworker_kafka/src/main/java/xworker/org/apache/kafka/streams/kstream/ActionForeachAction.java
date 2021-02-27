package xworker.org.apache.kafka.streams.kstream;

import org.apache.kafka.streams.kstream.ForeachAction;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ActionForeachAction implements ForeachAction<Object, Object>{
	Thing thing;
	ActionContext actionContext;
	
	public ActionForeachAction(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}

	public static ActionForeachAction create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new ActionForeachAction(self, actionContext);
	}

	@Override
	public void apply(Object key, Object value) {
		thing.getAction().run(actionContext, "key", key, "value", value);		
	}

}
