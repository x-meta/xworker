package xworker.lang.executor;

import java.util.HashMap;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.lang.executor.services.Log4jService;
import xworker.notification.NotificationManager;

public class NotificationExecutorService extends Log4jService{

	@Override
	public void requestUI(Thing request, ActionContext actionContext) {
		Thing notificationThing = World.getInstance().getThing("xworker.lang.executor.swt.NotificationExecutor");		
		Map<String, Object> requestVariables = request.doAction("getVariables", actionContext);
		if(requestVariables == null) {
			requestVariables = new HashMap<String, Object>();
		}
		
		actionContext.peek().put("request", request);
		actionContext.peek().put("requestContext", actionContext);
		requestVariables.put("__executorService__", this);
		requestVariables.put("request", request);
		requestVariables.put("requestContext", actionContext);
		requestVariables.put("requestVariables", requestVariables);
		actionContext.peek().put("requestVariables", requestVariables);
		
		//request,requestContext,requestVariables三个变量会带入到Notification的message的variables变量中
		//variables在创建SWT界面时会放入到变量上下文中
		
		NotificationManager.send(notificationThing, actionContext);		
	}

	@Override
	public void removeRequest(Thing request, ActionContext actionContext) {
		
	} 
}
