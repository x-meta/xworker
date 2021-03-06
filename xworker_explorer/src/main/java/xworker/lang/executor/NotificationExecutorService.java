package xworker.lang.executor;

import org.xmeta.Thing;
import org.xmeta.World;

import xworker.lang.executor.services.Log4jService;
import xworker.notification.Notification;
import xworker.notification.NotificationManager;

public class NotificationExecutorService extends Log4jService{

	@Override
	public void requestUI(ExecuteRequest request) {
		Thing notificationThing = World.getInstance().getThing("xworker.lang.executor.swt.NotificationExecutor");		
		request.setUIExecutorService(this);
		request.getActionContext().peek().put("request", request);
		request.getActionContext().peek().put("requestVariables", request.getVariables());
		
		Notification notification = NotificationManager.send(notificationThing, request.getActionContext());
		if(notification != null) {
			request.setData("__notification__", notification);
		}
	}

	@Override
	public void removeRequest(ExecuteRequest request) {
		Notification notifaction = (Notification) request.getData("__notification__");
		if(notifaction != null) {
			NotificationManager.removeNotification(notifaction);
		}
	} 
}
