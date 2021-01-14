package xworker.org.apache.kafka.streams.kstream.valuemappers;

import org.apache.kafka.streams.kstream.ValueMapper;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ActionValueMapper implements ValueMapper<Object, Object>{
	Thing thing;
	ActionContext actionContext;
	
	public ActionValueMapper(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	@Override
	public Object apply(Object value) {
		return thing.getAction().run(actionContext, "value", value);
	}

	public static ActionValueMapper create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new ActionValueMapper(self, actionContext);
	}


}
