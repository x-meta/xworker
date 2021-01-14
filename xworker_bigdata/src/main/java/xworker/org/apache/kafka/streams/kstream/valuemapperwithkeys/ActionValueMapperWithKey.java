package xworker.org.apache.kafka.streams.kstream.valuemapperwithkeys;

import org.apache.kafka.streams.kstream.ValueMapperWithKey;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ActionValueMapperWithKey implements ValueMapperWithKey<Object, Object, Object>{
	Thing thing;
	ActionContext actionContext;
	
	public ActionValueMapperWithKey(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	@Override
	public Object apply(Object key, Object value) {
		return thing.getAction().run(actionContext, "key", key, "value", value);
	}

	public static ActionValueMapperWithKey create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new ActionValueMapperWithKey(self, actionContext);
	}


}
