package xworker.ai.statemachine;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class StateMachineActions {
	public static void setState(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		StateMachine  stateMachine = self.doAction("getStateMachine", actionContext);
		String state = self.doAction("getState", actionContext);
		if(stateMachine != null) {
			stateMachine.setState(state);
		}
	}
}
