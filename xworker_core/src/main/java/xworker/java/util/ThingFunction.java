package xworker.java.util;

import java.util.function.Function;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingFunction implements Function<Object, Object>{
	Thing thing;
	ActionContext actionContext;
	
	public ThingFunction(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}

	public static ThingFunction create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new ThingFunction(self, actionContext);
	}

	@Override
	public Object apply(Object t) {
		return thing.doAction("apply", actionContext, "input", t);
	}

}
