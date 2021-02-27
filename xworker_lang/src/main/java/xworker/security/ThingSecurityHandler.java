package xworker.security;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

public class ThingSecurityHandler extends AbstractSecurityHandler{
	public ThingSecurityHandler(Thing thing){
		super(thing);
	}

	@Override
	public boolean doCheck(String env, String permission, String action, String path, ActionContext actionContext) {
		Thing thing = entry.getThing();
		if(thing == null){
			return true;
		}
		
		if(actionContext == null) {
			actionContext = new ActionContext();
		}
		Bindings bindings = actionContext.push();
		bindings.put("permission", permission);
		bindings.put("action", action);
		bindings.put("path", path);
		bindings.put("env", env);
		try{
			Boolean result = thing.doAction("doCheck", actionContext);
			if(result == null) {
				return false;
			}else {
				return result;
			}
		}finally{
			actionContext.pop();
		}
	}
	
	public static void regist(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String env = self.doAction("getEnv", actionContext);
		String permission = self.doAction("getPermission", actionContext);
		String action = self.doAction("getAction", actionContext);
		String path = self.doAction("getPath", actionContext);
		
		SecurityHandler handler = self.doAction("create", actionContext);
		if(handler != null) {
			SecurityManager.registSecurityHandler(env, permission, action, path, handler, actionContext);
		}
	}
	
	public static ThingSecurityHandler create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new ThingSecurityHandler(self);
	}
}
