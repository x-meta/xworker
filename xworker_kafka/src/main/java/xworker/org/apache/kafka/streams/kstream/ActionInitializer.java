package xworker.org.apache.kafka.streams.kstream;

import org.apache.kafka.streams.kstream.Initializer;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ActionInitializer implements Initializer<Object>{
	Thing thing;
	ActionContext actionContext;
	
	public ActionInitializer(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}

	public static ActionInitializer create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new ActionInitializer(self, actionContext);
	}

	@Override
	public Object apply() {
		return thing.getAction().run(actionContext);
	}

}
