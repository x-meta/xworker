package xworker.lang.state;

import org.xmeta.ActionContext;

public class StateActions {
	public static void pause(ActionContext actionContext) {
		State state = actionContext.getObject("state");
		state.setPause(true);
	}
	
	public static void resume(ActionContext actionContext) {
		State state = actionContext.getObject("state");
		state.setPause(false);
	}
	
	public static void exit(ActionContext actionContext) {
		State state = actionContext.getObject("state");
		state.exit();
	}
	
	public static void stateContinue(ActionContext actionContext) {
		State state = actionContext.getObject("state");
		state.doit = false;
	}
}
