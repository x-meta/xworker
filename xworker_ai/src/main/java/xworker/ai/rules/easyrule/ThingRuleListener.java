package xworker.ai.rules.easyrule;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.RuleListener;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

public class ThingRuleListener implements RuleListener{
	Thing thing;
	
	public ThingRuleListener(Thing thing) {
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
	public boolean beforeEvaluate(Rule rule, Facts facts) {
		Object result = thing.doAction("beforeEvaluate", getActionContext(facts), "rule", rule, "facts", facts);
		return UtilData.isTrue(result);
	}

	@Override
	public void afterEvaluate(Rule rule, Facts facts, boolean evaluationResult) {
		thing.doAction("afterEvaluate", getActionContext(facts), "rule", rule, "facts", facts, 
				"evaluationResult", evaluationResult);		
	}

	@Override
	public void beforeExecute(Rule rule, Facts facts) {
		thing.doAction("beforeExecute", getActionContext(facts), "rule", rule, "facts", facts);
		
	}

	@Override
	public void onSuccess(Rule rule, Facts facts) {
		thing.doAction("onSuccess", getActionContext(facts), "rule", rule, "facts", facts);
	}

	@Override
	public void onFailure(Rule rule, Facts facts, Exception exception) {
		thing.doAction("onFailure", getActionContext(facts), "rule", rule, "facts", facts, "exception", exception);
	}

}
