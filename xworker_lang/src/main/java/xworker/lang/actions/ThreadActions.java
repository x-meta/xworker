package xworker.lang.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThreadActions {
	public static void sleep(ActionContext actionContext) throws InterruptedException{
		Thing self = actionContext.getObject("self");
		
		long millis = (Long) self.doAction("getMillis", actionContext);
		Thread.sleep(millis);
	}
}
