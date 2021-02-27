package xworker.org.apache.kafka.streams.kstream;

import org.apache.kafka.streams.kstream.Merger;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingMerger implements Merger<Object, Object>{
	Thing thing;
	ActionContext actionContext;
	
	public ThingMerger(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}

	public static ThingMerger create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new ThingMerger(self, actionContext);
	}
	
	@Override
	public Object apply(Object aggKey, Object aggOne, Object aggTwo) {
		return thing.doAction("apply", actionContext, "aggKey", aggKey, "aggOne", aggOne, "aggTwo", aggTwo);
	}

}
