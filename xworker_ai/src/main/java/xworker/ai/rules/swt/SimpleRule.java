package xworker.ai.rules.swt;

import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.MapData;

import xworker.swt.util.SwtUtils;
import xworker.swt.util.ThingCompositeCreator;
import xworker.util.UtilData;

public class SimpleRule extends MapData implements Comparable<SimpleRule>{
	SimpleRulesComposite engine;
	Thing thing;
	ActionContext actionContext;
	int weight;
	Composite composite;
	ActionContext ac;
	
	public SimpleRule(SimpleRulesComposite engine, Thing thing, ActionContext actionContext) {
		this.engine = engine;
		this.thing = thing;
		this.actionContext = actionContext;
		this.weight = thing.doAction("getWeight", actionContext);
	}
	
	public boolean evaluate() {
		return UtilData.isTrue(thing.doAction("if", actionContext, "engine", engine, "rule", this));
	}
	
	public void execute() {
		thing.doAction("then", actionContext, "engine", engine, "rule", this);
	}
	
	public Thing getCompositeThing() {
		return thing.doAction("getComposite", actionContext);
	}
	
	
	public SimpleRulesComposite getEngine() {
		return engine;
	}

	public Thing getThing() {
		return thing;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}

	public int getWeight() {
		return weight;
	}

	public Composite getComposite() {
		return composite;
	}

	public ActionContext getAc() {
		return ac;
	}

	public static void then(ActionContext actionContext) {
		SimpleRulesComposite engine = actionContext.getObject("engine");
		SimpleRule rule = actionContext.getObject("rule");
		
		if(rule.composite == null || rule.composite.isDisposed()) {
			if(!"stack".equals(engine.type)) {
				//清空父节点
				for(Control child : engine.composite.getChildren()) {
					child.dispose();
				}
			}
			
			//创建子节点的Composite
			Thing compositeThing = rule.getCompositeThing();
			actionContext.peek().put("parent", engine.composite);
			ThingCompositeCreator cc = SwtUtils.createCompositeCreator(rule.getThing(), actionContext);
			rule.ac = cc.getNewActionContext();
			rule.ac.put("engine", engine);
			rule.ac.put("rule", rule);
			
			cc.setCompositeThing(compositeThing);
			cc.addChildFilter("Composite");
			cc.addChildFilter("actions");
			
			rule.composite = cc.create();			
		}
		
		rule.thing.doAction("init", rule.actionContext, "engine", engine, "rule", rule);
		
		if(rule.composite != null && !rule.composite.isDisposed() && "stack".equals(engine.type)) {
			StackLayout layout = (StackLayout) engine.composite.getLayout();
			layout.topControl = rule.composite;
		}
		
		engine.composite.layout();
	}

	@Override
	public int compareTo(SimpleRule o) {
		if(weight > o.weight) {
			return -1;
		}else if(weight == o.weight) {
			return 0;
		}else {
			return 1;
		}
	}
}
