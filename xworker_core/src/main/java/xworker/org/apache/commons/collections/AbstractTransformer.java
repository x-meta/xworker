package xworker.org.apache.commons.collections;

import org.apache.commons.collections.Transformer;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class AbstractTransformer implements Transformer {
	Thing thing;
	ActionContext actionContext;
	
	public AbstractTransformer(Thing thing, ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	@Override
	public Object transform(Object object) {
		return thing.doAction("transform", actionContext, "object", object);
	}
	
	public static AbstractTransformer create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		return new AbstractTransformer(self, actionContext);
	}
}
