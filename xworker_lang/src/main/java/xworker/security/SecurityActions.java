package xworker.security;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.util.UtilAction;

public class SecurityActions {
	public static void securityManagerRegist(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		for(Thing child : self.getChilds()) {
			child.doAction("regist", actionContext);
		}
	}
	
	public static Object checkPermission(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String env = self.doAction("getEnv", actionContext);
		List<String> permissions = self.doAction("getPermissions", actionContext);
		String action = self.doAction("getAction", actionContext);
		String path = self.doAction("getPath", actionContext);
		
		String childName = "Then";
		for(String permission : permissions) {
			if(xworker.security.SecurityManager.doCheck(env, permission, action, path, actionContext) == false) {
				childName = "Else";
				break;
			}
		}
		
		Object result = null;
		for(Thing child : self.getChilds(childName)) {
			result = UtilAction.runChildActions(child.getChilds(), actionContext);
		}
		
		return result;
	}
}
