package xworker.lang.executor;

import java.util.HashMap;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ExecuteRequest {
	Thing thing;
	ActionContext actionContext;
	Map<String, Object> variables;
	ExecutorService executorService;
	ExecutorService uiExecutorService;
	Map<String, Object> datas = new HashMap<String, Object>();
	
	public ExecuteRequest(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
		this.variables = thing.doAction("getVariables", actionContext);
		if(variables == null) {
			variables = new HashMap<String, Object>();
		}
		variables.put("request", this);
		this.executorService = Executor.getExecutorService();
	}
	
	public void setUIExecutorService(ExecutorService executorService) {
		this.uiExecutorService = executorService;
	}
	
	public void setData(String key, Object value) {
		datas.put(key, value);
	}
	
	public Object getData(String key) {
		return datas.get(key);
	}
	
	public Object doAction(String name) {
		actionContext.peek().putAll(variables);
		return thing.doAction(name, actionContext);
	}
	
	public Object doAction(String name, Map<String, Object> params) {
		actionContext.peek().putAll(variables);
		if(params != null) {
			actionContext.peek().putAll(params);
		}
		
		return thing.doAction(name, actionContext);
	}
	
	public Object doAction(String name, Object ... params) {
		actionContext.peek().putAll(variables);
		return thing.doAction(name, actionContext, params);
	}
	
	public void finish() {
		if(uiExecutorService != null) {
			uiExecutorService.removeRequest(this);
		}else {
			executorService.removeRequest(this);
		}
	}
	
	public Object getVariable(String key) {
		return variables.get(key);
	}
	
	public Object createSWT(Object parent, ActionContext actionContext) {
		actionContext.g().put("request", this);		
		actionContext.peek().put("parent", parent);
		
		return thing.doAction("createSWT", actionContext);
	}

	public Thing getThing() {
		return thing;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}

	public Map<String, Object> getVariables() {
		return variables;
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public ExecutorService getUiExecutorService() {
		return uiExecutorService;
	}
	
	
}
