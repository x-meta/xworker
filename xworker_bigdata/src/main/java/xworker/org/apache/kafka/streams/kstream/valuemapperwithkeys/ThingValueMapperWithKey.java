package xworker.org.apache.kafka.streams.kstream.valuemapperwithkeys;

import org.apache.kafka.streams.kstream.ValueMapperWithKey;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingValueMapperWithKey implements ValueMapperWithKey<Object, Object, Object>{
	Thing thing;
	ActionContext actionContext;
	
	public ThingValueMapperWithKey(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}

	public static ThingValueMapperWithKey create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new ThingValueMapperWithKey(self, actionContext);
	}

	@Override
	public Object apply(Object key, Object value) {
		return thing.doAction("apply", actionContext, "key", key, "value", value);
	}


}
