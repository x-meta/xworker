package xworker.statemachine;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;

/**
 * 状态机。
 * 
 * @author zyx
 *
 */
public class StateMachine {
	/** 状态机事物 */
	public Thing thing;
	
	/** 当前状态的事物 */
	public Thing currentState;
	
	/** 下一个状态的事物，即将要设置为下一个状态的事物 */
	public Thing nextState;
	
	/** 状态机所在的变量上下文 */
	public ActionContext actionContext;
	
	public StateMachine(Thing thing, ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
		
		//初始状态
		Thing initState = thing.doAction("getInitState", actionContext);
		if(initState != null){
			currentState = initState;
			currentState.doAction("enter", actionContext, "stateMachine", this);
		}
	}
	
	/**
	 * 处理消息。
	 * 
	 * @param message
	 */
	public void processMessage(Object message){
		if(currentState != null){
			Thing state = currentState;
			while(!UtilData.isTrue(state.doAction("processMessage", actionContext, "message", message))){
				state = state.getParent();
				if(state == null){
					break;
				}
				if(!"State".equals(state.getThingName())){
					break;
				}				
			}
		}
		
		changeState();
	}
	
	public void setNextState(Thing state){
		this.nextState = state;
	}
	
	public void setNextState(String statePath){
		this.nextState = World.getInstance().getThing(statePath);
	}
	
	public  void changeState(){
		//是否切换状态
		if(nextState != null){
			//原有状态退出
			currentState.doAction("exit", actionContext,  "stateMachine", this);
			
			//切换状态
			currentState = nextState;
			nextState = null;
			currentState.doAction("enter", actionContext, "stateMachine", this);
		}
	}
		
	public static StateMachine create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		return new StateMachine(self, actionContext);
	}
	
	public static StateMachine swtCreate(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		StateMachine machine = new StateMachine(self, actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), machine);
		return machine;
	}
	
	public static void processMessage(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Object message = self.doAction("getMessage", actionContext);
		StateMachine stateMachine = self.doAction("getStateMachine", actionContext);
		
		stateMachine.processMessage(message);
	}
	
	public static void changeState(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Thing nextState = self.doAction("getNextState", actionContext);
		StateMachine stateMachine = self.doAction("getStateMachine", actionContext);
		
		stateMachine.setNextState(nextState);
		stateMachine.changeState();
	}
}
