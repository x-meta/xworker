package xworker.lang.actions;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

import bsh.EvalError;
import bsh.Interpreter;

public class BeanShellAction {
	public static Object run(ActionContext actionContext) throws EvalError {
		Bindings bindings = actionContext.getScope(actionContext.getScopesSize() - 2);		
		
		World world = bindings.world;
		Action action = null;
		if(bindings.getCaller() instanceof Thing){
			Thing actionThing = (Thing) bindings.getCaller();
			action = actionThing.getAction();
		}else{
			action = (Action) bindings.getCaller();
		}
		
		if(action == null){
			return null;
		}
		
		Interpreter i = new Interpreter();
		i.set("ac", actionContext);
		i.set("world", world);
		
		if(action.getThing().getBoolean("mergeActionContext")) {
			for(String key : actionContext.keySet()) {
				i.set(key, actionContext.get(key));
			}
		}
		
		String code = action.getThing().getStringBlankAsNull("code");
		if(code == null || "".equals(code)) {
			return null;
		}else {
			return i.eval(code);
		}
	}
}
