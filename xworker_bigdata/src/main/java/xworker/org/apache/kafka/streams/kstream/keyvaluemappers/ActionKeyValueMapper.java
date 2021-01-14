package xworker.org.apache.kafka.streams.kstream.keyvaluemappers;

import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ActionKeyValueMapper implements KeyValueMapper<Object, Object, Object>{
	Thing thing;
	ActionContext actionContext;
	
	public ActionKeyValueMapper(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	@Override
	public Object apply(Object key, Object value) {
		return thing.getAction().run(actionContext, "key", key, "value", value);
	}

	public static ActionKeyValueMapper create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new ActionKeyValueMapper(self, actionContext);
	}


}
