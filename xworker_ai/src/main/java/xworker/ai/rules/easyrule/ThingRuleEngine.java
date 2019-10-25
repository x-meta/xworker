package xworker.ai.rules.easyrule;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.RuleListener;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.api.RulesEngineListener;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.core.InferenceRulesEngine;
import org.jeasy.rules.core.RuleBuilder;
import org.jeasy.rules.core.RulesEngineParameters;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingRuleEngine {
	public static void run(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
			
		RulesEngineParameters params = new RulesEngineParameters();
		if(self.get("skipOnFirstAppliedRule") != null) {
			params.setSkipOnFirstAppliedRule(self.getBoolean("skipOnFirstAppliedRule"));
		}
		
		if(self.get("skipOnFirstFailedRule") != null) {
			params.setSkipOnFirstFailedRule(self.getBoolean("skipOnFirstFailedRule"));
		}
		
		if(self.get("skipOnFirstNonTriggeredRule") != null) {
			params.setSkipOnFirstNonTriggeredRule(self.getBoolean("skipOnFirstNonTriggeredRule"));
		}
		
		if(self.get("priorityThreshold") != null) {
			params.setPriorityThreshold(self.getInt("priorityThreshold"));
		}
		
		Rules rules = new Rules();
		RulesEngine engine = null;
		if("inference".equals(self.getString("engine"))) {
			engine = new InferenceRulesEngine(params);
		}else {
			engine = new DefaultRulesEngine(params);
		}
		
		Facts facts = new Facts();
		facts.put("actionContext", actionContext);
		facts.put("engineThing", self);
		actionContext.peek().put("engine", engine);
		actionContext.peek().put("rules", rules);
		actionContext.peek().put("facts", facts);
		
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext, "engine", engine, "rules", rules);
		}
		
		//初始化facts
		self.doAction("initFacts", actionContext);
		
		//触发引擎
		engine.fire(rules, facts);
	}
	
	public static void createRule(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Rules rules = actionContext.getObject("rules");
		
		RuleBuilder rb = new RuleBuilder();
		rb.name(self.getMetadata().getName());
		rb.description(self.getMetadata().getDescription());
		rb.priority(self.getInt("priority"));
		rb.when(new ThingCondition(self));
		rb.then(new ThingAction(self));
		
		rules.register(rb.build());
	}
	
	public static void createRuleListener(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		RulesEngine engine = actionContext.getObject("engine");
		
		RuleListener ruleListener = new ThingRuleListener(self);
		if(engine instanceof DefaultRulesEngine) {
			((DefaultRulesEngine) engine).registerRuleListener(ruleListener);
		}else if(engine instanceof InferenceRulesEngine) {
			((InferenceRulesEngine) engine).registerRuleListener(ruleListener);
		}
	}
	
	public static void createRulesEngineListener(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		RulesEngine engine = actionContext.getObject("engine");
		
		RulesEngineListener	ruleListener = new ThingRuleEngineListener(self);
		if(engine instanceof DefaultRulesEngine) {
			((DefaultRulesEngine) engine).registerRulesEngineListener(ruleListener);
		}else if(engine instanceof InferenceRulesEngine) {
			((InferenceRulesEngine) engine).registerRulesEngineListener(ruleListener);
		}
	}
}
