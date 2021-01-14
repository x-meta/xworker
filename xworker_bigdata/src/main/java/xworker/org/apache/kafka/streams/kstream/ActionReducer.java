package xworker.org.apache.kafka.streams.kstream;

import org.apache.kafka.streams.kstream.Reducer;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ActionReducer  implements Reducer <Object>{
	Thing thing;
	ActionContext actionContext;
	
	public ActionReducer(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}

	public static ActionReducer create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new ActionReducer(self, actionContext);
	}

	@Override
	public Object apply(Object value1, Object value2) {
		return thing.getAction().run(actionContext, "value1", value1, "value2", value2);
	}


}
