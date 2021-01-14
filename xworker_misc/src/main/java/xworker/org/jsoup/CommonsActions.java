package xworker.org.jsoup;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.util.UtilAction;

public class CommonsActions {
	public static void runGroovy(ActionContext actionContext) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException {
		Thing self = actionContext.getObject("self");
		
		UtilAction.runGroovyAction(self, actionContext);
	}
	
	public static void runAction(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		for(Thing child : self.getChilds()) {
			child.getAction().run(actionContext);
		}
	}
	
	public static void reference(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Thing ref = self.doAction("getRef", actionContext);
		if(ref != null) {
			ref.doAction("run", actionContext);
		}
	}
}
