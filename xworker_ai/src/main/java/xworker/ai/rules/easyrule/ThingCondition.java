package xworker.ai.rules.easyrule;

import org.jeasy.rules.api.Condition;
import org.jeasy.rules.api.Facts;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.util.UtilData;

public class ThingCondition implements Condition{
	Thing thing;
	
	public ThingCondition(Thing thing) {
		this.thing = thing;
	}
	
	
	@Override
	public boolean evaluate(Facts facts) {
		Object result = null;
		ActionContext actionContext = facts.get("actionContext");
		if(actionContext == null) {
			actionContext = new ActionContext();
		}
		
		result = thing.doAction("if", actionContext, "facts", facts);		
		return UtilData.isTrue(result);
	}

}
