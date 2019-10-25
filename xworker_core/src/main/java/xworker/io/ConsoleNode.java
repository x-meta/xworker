package xworker.io;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ConsoleNode {
	Thing thing;
	ActionContext actionContext;
	
	public ConsoleNode(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	public Object run() {
		return thing.doAction("run", actionContext);
	}
}
