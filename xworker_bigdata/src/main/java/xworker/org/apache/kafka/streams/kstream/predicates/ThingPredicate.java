package xworker.org.apache.kafka.streams.kstream.predicates;

import org.apache.kafka.streams.kstream.Predicate;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

public class ThingPredicate implements Predicate<Object, Object>{
	Thing thing;
	ActionContext actionContext;
	
	public ThingPredicate(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	@Override
	public boolean test(Object key, Object value) {
		return UtilData.isTrue(thing.doAction("test", actionContext, "key", key, "value", value));
	}

	public static ThingPredicate create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new ThingPredicate(self, actionContext);
	}
}
