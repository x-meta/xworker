package xworker.ai.rules.easyrule;

import org.jeasy.rules.api.Action;
import org.jeasy.rules.api.Facts;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingAction implements Action{
	Thing thing;
	
	public ThingAction(Thing thing) {
		this.thing = thing;
	}
	
	@Override
	public void execute(Facts facts) throws Exception {
		ActionContext actionContext = facts.get("actionContext");
		if(actionContext == null) {
			actionContext = new ActionContext();
		}
		
		thing.doAction("then", actionContext, "facts", facts);
	}
	
}
