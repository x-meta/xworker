package xworker.ai.statemachine.swt;

import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.ai.statemachine.SimpleStateMachine;
import xworker.ai.statemachine.ThingState;
import xworker.swt.util.SwtUtils;
import xworker.swt.util.ThingCompositeCreator;

public class SimpleStateMachineComposite {
	public static Composite createStateMachine(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Thing rootComposite = self.getThing("RootComposite@0");
		if(rootComposite == null) {
			rootComposite = new Thing("xworker.swt.widgets.Composite");
		}
		
		ThingCompositeCreator cc = SwtUtils.createCompositeCreator(self, actionContext);
		cc.setCompositeThing(rootComposite);
		cc.addChildFilter("RootComposite");
		cc.addChildFilter("State");
		Composite composite = cc.create();
		
		//子节点的类型
		String type = self.doAction("getType", actionContext);
		if("stack".equals(type)) {
			composite.setLayout(new StackLayout());
		}else {
			composite.setLayout(new FillLayout());
		}		
		
		ActionContext ac = cc.getNewActionContext();
		ac.put("type", type);
		ac.put("composite", composite);		
	
		//简单状态机
		SimpleStateMachine stateMachine = new SimpleStateMachine(self, actionContext);
		stateMachine.put("composite", composite);
		stateMachine.put("type", type);		
		stateMachine.put("parentContext", actionContext);		
		ac.put("stateMachine", stateMachine);
		stateMachine.start();
		
		actionContext.g().put(self.getMetadata().getName(), stateMachine);
		
		return composite;
	}
	
	public static void stateEnter(ActionContext actionContext) {
		SimpleStateMachine stateMachine = actionContext.getObject("stateMachine");
		ThingState state = actionContext.getObject("state");
		String type = stateMachine.getString("type");
		ActionContext parentContext = stateMachine.getObject("parentContext"); //创建stateMachine所在的SWT上下文
		
		//创建compsite
		Composite stateComposite = state.getObject("composite"); 
		if(stateComposite == null || stateComposite.isDisposed()) {
			Thing compositeThing = state.getThing().doAction("getComposite", parentContext);
			if(compositeThing != null) {
				actionContext.peek().put("parent", stateMachine.get("composite"));
				ThingCompositeCreator cc = SwtUtils.createCompositeCreator(state.getThing(), actionContext);
				cc.setCompositeThing(compositeThing);
				ActionContext ac = cc.getNewActionContext();
				ac.put("stateMachine", stateMachine);
				ac.put("state", state);
				ac.put("parentContext", stateMachine.getActionContext());				
				
				cc.addChildFilter("Composite");
				stateComposite = cc.create();
				state.set("actionContext", ac);
				state.set("composite", stateComposite);
			}
		}
		
		//显示到根composite
		Composite rootComposite = stateMachine.getObject("composite");
		if(stateComposite != null && !stateComposite.isDisposed()) {			
			if("stack".equals(type)) {
				StackLayout layout = (StackLayout) rootComposite.getLayout();
				layout.topControl = stateComposite;
				rootComposite.layout();
			}
		}
		
		rootComposite.layout();
		//rootComposite.getParent().layout();
	}
	
	public static void stateExit(ActionContext actionContext) {
		SimpleStateMachine stateMachine = actionContext.getObject("stateMachine");
		ThingState state = actionContext.getObject("state");
		String type = stateMachine.getString("type");
		
		//销毁compsite，如果不是stack模式
		Composite stateComposite = state.getObject("composite"); 
		if(stateComposite != null && !stateComposite.isDisposed()) {
			if(!"stack".equals(type)) {
				stateComposite.dispose();
			}
		}
	}
}
