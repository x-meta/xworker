package xworker.org.apache.kafka.streams.kstream;

import org.apache.kafka.streams.kstream.Merger;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ActionMerger implements Merger<Object, Object>{
	Thing thing;
	ActionContext actionContext;
	
	public ActionMerger(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}

	public static ActionMerger create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new ActionMerger(self, actionContext);
	}
	
	@Override
	public Object apply(Object aggKey, Object aggOne, Object aggTwo) {
		return thing.getAction().run(actionContext, "aggKey", aggKey, "aggOne", aggOne, "aggTwo", aggTwo);
	}



}
