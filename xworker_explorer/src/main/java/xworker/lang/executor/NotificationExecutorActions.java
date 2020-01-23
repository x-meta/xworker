package xworker.lang.executor;

import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.ActionContainer;
import org.xmeta.util.UtilData;

import xworker.lang.system.message.Message;
import xworker.notification.Notification;
import xworker.notification.NotificationManager;

public class NotificationExecutorActions {
	private static boolean isExecuted(Map<String, Object> values) {
		//如果已经执行过了，则不用再做
		String key = "__executed__";
		if(UtilData.isTrue(values.get(key))) {
			return true;
		}else {
			values.put(key, true);
			return false;
		}
	}
	
	public static void okButtonSelection(ActionContext actionContext) {
		final Map<String, Object> values = actionContext.getObject("requestVariables");
		if(isExecuted(values)) {
			return;
		}
		ActionContainer actions = actionContext.getObject("actions");
		final Thing request = actionContext.getObject("request");
		final ActionContext requestContext = actionContext.getObject("requestContext");		
		
		Object r = null;
		if(actions != null) {
			Object validate = actions.doAction("validate", requestContext);
			if(validate != null && UtilData.isTrue(validate) == false) {
				//校验失败
				return;
			}
			
			//异步执行结果
			r = actions.doAction("getResult", actionContext);	
		}
		final Object result = r;
		new Thread(new Runnable() {
			public void run() {
				try {
					ExecutorService executorService = (ExecutorService) values.get("__executorService__");
					Executor.push(executorService);
					
					requestContext.peek().putAll(values);
					request.doAction("ok", requestContext, "result", result);
				}catch(Exception e) {
					Executor.error("NotificationExecutor", "Execute request ok error, path=" + request.getMetadata().getPath(), e);
				}finally {
					Executor.pop();
				}
			}
		}).start();
				
		Notification notifiction = actionContext.getObject("notification");
		NotificationManager.removeNotification(notifiction);
	}
	
	public static void cancelButtonSelection(ActionContext actionContext) {
		final Map<String, Object> values = actionContext.getObject("requestVariables");
		if(isExecuted(values)) {
			return;
		}
		
		final Thing request = actionContext.getObject("request");
		final ActionContext requestContext = actionContext.getObject("requestContext");
		new Thread(new Runnable() {
			public void run() {
				try {
					ExecutorService executorService = (ExecutorService) values.get("__executorService__");
					Executor.push(executorService);
					
					requestContext.peek().putAll(values);
					request.doAction("cancel", requestContext);
				}catch(Exception e) {
					Executor.error("NotificationExecutor", "Execute request cancel error, path=" + request.getMetadata().getPath(), e);
				} finally {
					Executor.pop();
				}
			}
		}).start();
		
		
		Notification notifiction = actionContext.getObject("notification");
		NotificationManager.removeNotification(notifiction);
	}
	
	public static void doinit(ActionContext actionContext) {
		ActionContext ac = new ActionContext();
		ac.put("parent", actionContext.get("requestComposite"));
		ac.put("request", actionContext.get("request"));
		ac.put("requestContext", actionContext.get("requestContext"));
		//把传入的变量带入到变量上下文中
		Message message = actionContext.getObject("message");
		if(message != null && message.getVariables() != null) {
			ac.putAll(message.getVariables());
		}
		ac.put("requestVariables", actionContext.get("requestVariables"));

		ExecuteRequest request = actionContext.getObject("request");
		request.createSWT(actionContext.get("mainComposite"), ac);
		//request.doAction("createSWT", ac);
		//println actionContext.get("request");
		actionContext.g().put("actions", ac.get("actions"));
	}
	
	public static Object getTimeout(ActionContext actionContext) {
		ExecuteRequest request = actionContext.getObject("request");
		Thing requestThing = request.getThing();
		return requestThing.doAction("getTimeout", actionContext);
	}
	
	public static Object getLabel(ActionContext actionContext) {
		ExecuteRequest request = actionContext.getObject("request");
		Thing requestThing = request.getThing();
		String label = requestThing.doAction("getLabel", actionContext);
		if(label == null || "".equals(label)) {
			return requestThing.getMetadata().getLabel();
		}else {
			return label;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void onFinished(ActionContext actionContext) {
		/*
		Notification notification = actionContext.getObject("notification");
		Map<String, Object> mvalues = notification.getMessage().getVariables();		
		final Map<String, Object> values = (Map<String, Object>) mvalues.get("requestVariables");
		if(isExecuted(values)) {
			return;
		}
		
		final Thing request = (Thing) mvalues.get("request");
		final ActionContext requestContext = (ActionContext) mvalues.get("requestContext");
		new Thread(new Runnable() {
			public void run() {
				try {
					ExecutorService executorService = (ExecutorService) values.get("__executorService__");
					Executor.push(executorService);
					
					requestContext.peek().putAll(values);
					request.doAction("cancel", requestContext);
				}catch(Exception e) {
					Executor.error("NotificationExecutor", "Execute request cancel error, path=" + request.getMetadata().getPath(), e);
				} finally {
					Executor.pop();
				}
			}
		}).start();		*/
	}
	
	public static String getId(ActionContext actionContext) {
		ExecuteRequest request = actionContext.getObject("request");
		Thing requestThing = request.getThing();
		String messageId =  requestThing.doAction("getMessageId", actionContext);
		if(messageId == null || "".equals(messageId)) {
			return requestThing.getMetadata().getPath();
		}else {
			return messageId;
		}
	}
}
