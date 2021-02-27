package xworker.org.apache.kafka.streams.kstream;

import org.apache.kafka.streams.kstream.Aggregator;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingAggregator implements Aggregator<Object, Object, Object>{
	Thing thing;
	ActionContext actionContext;
	
	public ThingAggregator(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}

	public static ThingAggregator create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new ThingAggregator(self, actionContext);
	}

	@Override
	public Object apply(Object key, Object value, Object aggregate) {
		return thing.doAction("apply", actionContext, "key", key, "value", value, "aggregate", aggregate);
	}
}
