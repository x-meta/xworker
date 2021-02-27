package xworker.lang.state;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ActionActions {
	public static void execAbstractAction(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		StateEvent state = actionContext.getObject("state");
		if(state.doit == false) {
			return;
		}
		
		for(Thing child : self.getChilds()) {
			child.getAction().run(actionContext);
			
			if(state.doit == false) {
				return;
			}
		}
	}
	
	public static void execAction(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		StateEvent state = actionContext.getObject("state");
		if(state.doit == false) {
			return;
		}
		
		self.getAction().run(actionContext);
	}
	
	public static void exit(ActionContext actionContext) {
		State state = actionContext.getObject("state");
		state.exit();
	}
	
	public static void pause(ActionContext actionContext) {
		State state = actionContext.getObject("state");
		state.setPause(true);
	}
	
	public static void execContinue(ActionContext actionContext) {
		State state = actionContext.getObject("state");
		state.doit = false;
	}
	
	public static void resume(ActionContext actionContext) {
		State state = actionContext.getObject("state");
		state.setPause(false);
	}
}
