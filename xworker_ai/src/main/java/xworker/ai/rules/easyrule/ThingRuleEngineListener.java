package xworker.ai.rules.easyrule;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngineListener;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingRuleEngineListener implements RulesEngineListener{
	Thing thing;
	
	public ThingRuleEngineListener(Thing thing) {
		this.thing = thing;
	}
	
	private ActionContext getActionContext(Facts facts) {
		ActionContext actionContext = facts.get("actionContext");
		if(actionContext == null) {
			actionContext = new ActionContext();
		}
		
		return actionContext;
	}
	
	@Override
	public void beforeEvaluate(Rules rules, Facts facts) {
		thing.doAction("beforeEvaluate", getActionContext(facts), "rules", rules, "facts", facts);	
	}

	@Override
	public void afterExecute(Rules rules, Facts facts) {
		thing.doAction("afterExecute", getActionContext(facts), "rules", rules, "facts", facts);	
	}

}
