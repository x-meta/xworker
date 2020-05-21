package xworker.util.autoplay;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class AutoPlayThingNode implements AutoPlayNode{
	Thing thing;
	ActionContext actionContext;
	long delay = 0;
	AutoPlayNodeGroup parent = null;
	
	public AutoPlayThingNode(AutoPlayNodeGroup parent, Thing thing, ActionContext actionContext) {
		this.parent = parent;
		this.thing = thing;
		this.actionContext = actionContext;		
		this.delay = thing.doAction("getDelay", actionContext);
	}

	@Override
	public long getDelay() {
		return delay;
	}

	@Override
	public void run(boolean step) {
		thing.doAction("run", actionContext);
	}
	
	public static AutoPlayThingNode create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Object parent = actionContext.get("parent");
		if(parent instanceof AutoPlayNodeGroup) {
			return new AutoPlayThingNode((AutoPlayNodeGroup) parent, self, actionContext);
		}else {
			return new AutoPlayThingNode(null, self, actionContext);
		}		
	}

	public Thing getThing() {
		return thing;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}

	@Override
	public AutoPlayNodeGroup getParnet() {
		// TODO Auto-generated method stub
		return null;
	}
		
}
