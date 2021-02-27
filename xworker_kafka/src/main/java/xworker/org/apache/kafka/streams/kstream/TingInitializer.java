package xworker.org.apache.kafka.streams.kstream;

import org.apache.kafka.streams.kstream.Initializer;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class TingInitializer implements Initializer<Object>{
	Thing thing;
	ActionContext actionContext;
	
	public TingInitializer(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}

	public static TingInitializer create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new TingInitializer(self, actionContext);
	}

	@Override
	public Object apply() {
		return thing.doAction("apply", actionContext);
	}

}
