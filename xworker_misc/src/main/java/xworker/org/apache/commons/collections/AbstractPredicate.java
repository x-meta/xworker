package xworker.org.apache.commons.collections;

import org.apache.commons.collections.Predicate;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class AbstractPredicate implements Predicate{
	Thing thing;
	ActionContext actionContext;
	
	public AbstractPredicate(Thing thing, ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	@Override
	public boolean evaluate(Object object) {
		return (Boolean) thing.doAction("evaluate", actionContext, "object", object);
	}
	
	public static AbstractPredicate create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		return new AbstractPredicate(self, actionContext);
	}
}
