package xworker.lang.executor;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.lang.executor.services.AbstractRequestService;
import xworker.lang.executor.services.Log4jService;
import xworker.notification.Notification;
import xworker.notification.NotificationManager;

import java.util.List;

public class NotificationExecutorService extends AbstractRequestService {

	@Override
	public Request requestUI(Thing thing, ActionContext actionContext) {
		Request request = new Request(thing, actionContext);
		Thing notificationThing = World.getInstance().getThing("xworker.lang.executor.swt.NotificationExecutor");		
		request.setUIExecutorService(this);
		request.getActionContext().peek().put("request", request);
		request.getActionContext().peek().put("requestVariables", request.getVariables());
		
		Notification notification = NotificationManager.send(notificationThing, request.getActionContext());
		if(notification != null) {
			request.setData("__notification__", notification);
		}

		return request;
	}

	@Override
	public List<Request> getRequests() {
		return null;
	}

	@Override
	public void removeRequest(Request request) {
		Notification notifaction = (Notification) request.getData("__notification__");
		if(notifaction != null) {
			NotificationManager.removeNotification(notifaction);
		}
	}

	@Override
	public Thread getThread() {
		return null;
	}
}
