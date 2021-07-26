package xworker.lang.executor;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ExecutorActions {
	public static Object requestCreateSWT(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		//Composite在描述者上定义
		Thing descriptor = self.getDescriptor();
		Thing composite = descriptor.getThing("Composite@0");
		if(composite != null) {
			return composite.doAction("create", actionContext, "requestUI", self);
		} else {
			return null;
		}
	}

	public static boolean isSupport(ActionContext actionContext){
		String platform = actionContext.getObject("platform");
		Thing self = actionContext.getObject("self");

		return true;
	}

	/**
	 * Request模型自己的reqestUI。
	 *
	 * @param actionContext
	 */
	public static void request(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		Executor.requestUI(self, actionContext);
	}

	/**
	 * RequestUI动作模型调用的RequestUI。
	 * @param actionContext
	 */
	public static void requestUI(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");

		Thing request = self.doAction("getRequest", actionContext);
		ExecutorService executorService = self.doAction("getExecutorService", actionContext);
		ActionContext ac = self.doAction("getActionContext", actionContext);
		if(ac == null){
			ac = actionContext;
		}

		try {
			if (executorService != null) {
				Executor.push(executorService);
			}

			if(request != null){
				Executor.requestUI(request, ac);
			}

			for(Thing req : self.getChilds("Request")){
				Executor.requestUI(req, ac);
			}
		}finally {
			if(executorService != null){
				Executor.pop();
			}
		}
	}
	
	public static Object requestUICreateSWT(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Thing request = self.doAction("getRequest", actionContext);
		if(request == null) {
			Executor.warn("RequestUI", "Request thing is null, path={}", self.getMetadata().getPath());
			return null;
		}else {
			return request.doAction("createSWT", actionContext);
		}
	}
	
	public static void log(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		String TAG = self.doAction("getTag", actionContext);
		if(TAG == null || "".equals(TAG)) {
			TAG = self.getMetadata().getName();
		}
		String level = self.doAction("getLevel", actionContext);
		String message = self.doAction("getMessage", actionContext);
		Throwable t = self.doAction("getThrowable", actionContext);
		List<Object> list = new ArrayList<Object>();
		for(Thing child : self.getChilds()) {
			if("actions".equals(child.getThingName())) {
				continue;
			}
			
			list.add(child.getAction().run(actionContext));
		}
		Object[] array = new Object[list.size()];
		list.toArray(array);
		ExecutorService service = self.doAction("getExecutorService", actionContext);
		try {
			if(service != null) {
				Executor.push(service);
			}
		
			if("debug".equals(level)) {
				if(array.length == 0) {
					if(t == null) {
						Executor.debug(TAG, message);
					}else {
						Executor.debug(TAG, message, t);
					}
				}else {
					Executor.debug(TAG, message, array);
				}
			}else if("trace".equals(level)) {
				if(array.length == 0) {
					if(t == null) {
						Executor.trace(TAG, message);
					}else {
						Executor.trace(TAG, message, t);
					}
				}else {
					Executor.trace(TAG, message, array);
				}
			}else if("warn".equals(level)) {
				if(array.length == 0) {
					if(t == null) {
						Executor.warn(TAG, message);
					}else {
						Executor.warn(TAG, message, t);
					}
				}else {
					Executor.warn(TAG, message, array);
				}
			}else if("error".equals(level)) {
				if(array.length == 0) {
					if(t == null) {
						Executor.error(TAG, message);
					}else {
						Executor.error(TAG, message, t);
					}
				}else {
					Executor.error(TAG, message, array);
				}
			}else {
				if(array.length == 0) {
					if(t == null) {
						Executor.info(TAG, message);
					}else {
						Executor.info(TAG, message, t);
					}
				}else {
					Executor.info(TAG, message, array);
				}
			}
		}finally {
			if(service != null) {
				Executor.pop();
			}
		}
	}
}
