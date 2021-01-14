package xworker.java.util;

import java.util.function.Function;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ActionFunction implements Function<Object, Object>{
	Thing thing;
	ActionContext actionContext;
	
	public ActionFunction(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}

	public static ActionFunction create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new ActionFunction(self, actionContext);
	}

	@Override
	public Object apply(Object t) {
		return thing.getAction().run(actionContext, "input", t);
	}

}
