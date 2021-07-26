package xworker.org.apache.commons.collections;

import org.apache.commons.collections.Factory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class AbstractFactory implements Factory{
	Thing thing;
	ActionContext actionContext;
	
	public AbstractFactory(Thing thing, ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	@Override
	public Object create() {
		return thing.doAction("doCreate", actionContext);
	}
	
	public static AbstractFactory create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		return new AbstractFactory(self, actionContext);
	}
}
