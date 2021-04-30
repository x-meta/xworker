package xworker.ai.rules.swt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.MapData;

import xworker.swt.util.SwtUtils;
import xworker.swt.util.ThingCompositeCreator;

public class SimpleRulesComposite extends MapData{
	Thing thing;
	ActionContext actionContext;
	String type;
	Composite composite;
	List<SimpleRule> rules = new ArrayList<SimpleRule>();
	
	public SimpleRulesComposite(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
		
		Thing rootComposite = thing.getThing("RootComposite@0");
		if(rootComposite == null) {
			rootComposite = new Thing("xworker.swt.widgets.Composite");
		}
		
		ThingCompositeCreator cc = SwtUtils.createCompositeCreator(thing, actionContext);
		cc.setCompositeThing(rootComposite);
		cc.addChildFilter("RootComposite");
		cc.addChildFilter("Rule");
		cc.addChildFilter("DefaultRule");
		cc.addChildFilter("actions");
		cc.addChildFilter("State");
		composite = cc.create();
		
		//子节点的类型
		this.type = thing.doAction("getType", actionContext);
		if("stack".equals(type)) {
			composite.setLayout(new StackLayout());
		}else {
			composite.setLayout(new FillLayout());
		}		
		
		for(Thing rule : thing.getChilds("Rule")) {
			rules.add(new SimpleRule(this, rule, actionContext));
		}
		Collections.sort(rules);
		
		//初始化
		thing.doAction("init", actionContext, "engine", this);
		
		//执行规则
		fire();
	}
	
	public void fire() {
		for(SimpleRule rule : rules) {
			if(rule.evaluate()) {
				rule.execute();
				return;
			}
		}
	}
	
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		SimpleRulesComposite engine = new SimpleRulesComposite(self, actionContext);
		engine.fire();
		
		actionContext.g().put(self.getMetadata().getName(), engine);
		return engine.composite;
	}
}
