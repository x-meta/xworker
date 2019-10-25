package xworker.notification;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class NotificationActions {
	public static Notification sendEvent(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		return NotificationManager.send(self, actionContext);
	}
	
	public static void removeNotification(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Notification notify = self.doAction("getNotification", actionContext);
		if(notify != null) {
			NotificationManager.removeNotification(notify);
		}
	}
}
