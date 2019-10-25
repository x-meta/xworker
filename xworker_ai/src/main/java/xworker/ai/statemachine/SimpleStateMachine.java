package xworker.ai.statemachine;

import java.util.HashMap;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class SimpleStateMachine extends AbstractStateMachine{
	State current;
	Thing thing;
	ActionContext actionContext;
	
	/** 状态的缓存 */
	Map<String, State> states = new HashMap<String, State>();
	
	public SimpleStateMachine(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
		
		//初始化
		thing.doAction("init", actionContext, "stateMachine", this);
	}
	
	public void start() {
		//设置初始状态
		String initState = thing.doAction("getInitState", actionContext);
		if(initState == null || "".equals(initState)) {
			Thing firstState = thing.getThing("State@0");
			if(firstState != null) {
				initState = firstState.getMetadata().getName();
			}
		}
		
		setState(initState);
	}
	
	public Thing getThing() {
		return thing;
	}
	
	public ActionContext getActionContext() {
		return actionContext;
	}
		
	@Override
	public void setState(String state) {
		if(state == null) {
			return;
		}
		
		//如果是当前状态，执行action
		if(current != null && current.getName().equals(state)) {
			executeCurrentState();
			return;
		}
		
		State newState = states.get(state);
		if(newState == null) {
			for(Thing child : thing.getChilds("State")) {
				if(state.equals(child.getMetadata().getName())) {
					newState = new ThingState(this, child, actionContext);
					states.put(state, newState);
					break;
				}
			}
		}
		
		if(newState != null) {
			//当前状态退出，如果退出失败，不进入下一个状态
			if(current != null) {
				Object result = current.exit();
				if(result instanceof Boolean && (Boolean) result == false) {
					return;
				}
			}
			
			current = newState;
			current.enter();
			executeCurrentState();
		}
	}
	
	private void executeCurrentState() {
		if(current != null) {
			Object result = current.doAction();
			
			if(result instanceof String) {
				setState((String) result);
			}
		}
	}

	@Override
	public State getCurrentState() {
		return current;
	}

	public static Object run(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		SimpleStateMachine stateMachine = new SimpleStateMachine(self, actionContext);
		stateMachine.start();
		
		return stateMachine;
	}
}
