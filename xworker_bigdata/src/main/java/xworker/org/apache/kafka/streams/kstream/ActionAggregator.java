package xworker.org.apache.kafka.streams.kstream;

import org.apache.kafka.streams.kstream.Aggregator;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ActionAggregator implements Aggregator<Object, Object, Object>{
	Thing thing;
	ActionContext actionContext;
	
	public ActionAggregator(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}

	public static ActionAggregator create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new ActionAggregator(self, actionContext);
	}

	@Override
	public Object apply(Object key, Object value, Object aggregate) {
		return thing.getAction().run(actionContext, "key", key, "value", value, "aggregate", aggregate);
	}


}
