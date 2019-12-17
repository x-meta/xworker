package xworker.swt.app;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ApplicationActions {
	public static void setTheme(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Application app = self.doAction("getApplication", actionContext);
		Thing theme = self.doAction("getTheme", actionContext);
		app.setTheme(theme);
	}
	
	public static void reset(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Application app = self.doAction("getApplication", actionContext);
		app.reset();
	}
}
