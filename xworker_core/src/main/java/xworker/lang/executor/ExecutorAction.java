package xworker.lang.executor;

import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.util.UtilData;

public class ExecutorAction {
	private static final String KEY = "__ExecutorAction__";
	Thing thing;
	ActionContext actionContext;
	
	public ExecutorAction(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = new ActionContext();
		
		Map<String, Object> values = thing.doAction("getVariables", actionContext);
		if(values != null) {
			this.actionContext.putAll(values);
		}
		this.actionContext.put("parentContext", actionContext);
		this.actionContext.put(KEY, this);
	}
	
	public void run() {		
		for(Thing request : thing.getChilds("Request")) {
			if(UtilData.isTrue(request.doAction("isNeedRequest", actionContext))) {
				Executor.requestUI(request, actionContext);
				
				return;
			}
		}
		
		Object result = thing.doAction("doAction", actionContext);
		if(UtilData.isTrue(result)) {
			run();
		}
	}
	
	//--------------------事物对应的方法------------------
	public static Object run(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		ExecutorAction ea = new ExecutorAction(self, actionContext);
		ea.run();
		return ea;
	}
	
	public static Object requestCreateSWT(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		for(Thing request : self.getChilds()) {
			if(!"actions".equals(request.getThingName())) {
				return request.doAction("createSWT", actionContext);
			}
		}

		Executor.warn("RequestUI", "Request thing is null, path={}", self.getMetadata().getPath());
		return null;
	}
	
	public static Object requestIsNeedRequest(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		String name = self.getMetadata().getName();
		if(self.getBoolean("notnull") && actionContext.getObject(name) == null) {
			return true;
		}
		
		return false;
	}
	
	public static void requestCancel(ActionContext actionContext) {	
		ExecutorAction ea = actionContext.getObject(KEY);
		ea.thing.doAction("cancel", actionContext);
	}
	
	public static void requestOk(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		String name = self.getMetadata().getName();
		
		Object result = actionContext.get("result");
		if(result == null && self.getBoolean("okOk")) {
			result = "ok";
		}
		actionContext.g().put(name, result);
		
		ExecutorAction ea = actionContext.getObject(KEY);
		ea.run();
	}
}


