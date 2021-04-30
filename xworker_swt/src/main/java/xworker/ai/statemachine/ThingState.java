package xworker.ai.statemachine;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.MapData;

/**
 * 使用事物模型表示的状态。
 * 
 * @author zyx
 *
 */
public class ThingState extends MapData implements State{
	Thing thing;
	ActionContext actionContext;
	StateMachine stateMachine;
	
	public ThingState(StateMachine stateMachine, Thing thing, ActionContext actionContext) {
		this.stateMachine = stateMachine;
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	@Override
	public Object enter() {
		return thing.doAction("enter", actionContext, "stateMachine", stateMachine, "state", this);
	}

	@Override
	public Object doAction() {
		return thing.doAction("doAction", actionContext, "stateMachine", stateMachine, "state", this);
	}

	@Override
	public Object exit() {
		return thing.doAction("exit", actionContext, "stateMachine", stateMachine, "state", this);		
	}

	@Override
	public StateMachine getStateMachine() {
		return stateMachine;
	}
	@Override
	public String getName() {
		return thing.getMetadata().getName();
	}

	public Thing getThing() {
		return thing;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}
	
	

}
