package xworker.org.apache.kafka.streams.kstream.valuemappers;

import org.apache.kafka.streams.kstream.ValueMapper;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingValueMapper implements ValueMapper<Object, Object>{
	Thing thing;
	ActionContext actionContext;
	
	public ThingValueMapper(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}

	public static ThingValueMapper create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new ThingValueMapper(self, actionContext);
	}

	@Override
	public Object apply(Object value) {
		return thing.doAction("apply", actionContext, "value", value);
	}

}
