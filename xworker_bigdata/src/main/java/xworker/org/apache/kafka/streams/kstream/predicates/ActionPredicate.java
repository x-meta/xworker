package xworker.org.apache.kafka.streams.kstream.predicates;

import org.apache.kafka.streams.kstream.Predicate;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

public class ActionPredicate implements Predicate<Object, Object>{
	Thing thing;
	ActionContext actionContext;
	
	public ActionPredicate(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	@Override
	public boolean test(Object key, Object value) {
		return UtilData.isTrue(thing.getAction().run(actionContext, "key", key, "object", value));
	}

	public static ActionPredicate create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new ActionPredicate(self, actionContext);
	}

}
