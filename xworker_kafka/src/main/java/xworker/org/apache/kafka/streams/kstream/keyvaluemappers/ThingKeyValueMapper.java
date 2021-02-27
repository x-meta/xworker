package xworker.org.apache.kafka.streams.kstream.keyvaluemappers;

import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingKeyValueMapper implements KeyValueMapper<Object, Object, Object>{
	Thing thing;
	ActionContext actionContext;
	
	public ThingKeyValueMapper(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}

	public static ThingKeyValueMapper create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new ThingKeyValueMapper(self, actionContext);
	}

	@Override
	public Object apply(Object key, Object value) {
		return thing.doAction("apply", actionContext, "key", key, "value", value);
	}

}
