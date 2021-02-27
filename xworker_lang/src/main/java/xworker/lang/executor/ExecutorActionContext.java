package xworker.lang.executor;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ExecutorActionContext {
	private static final String KEY = "__ExecutorContext_BACKUP_LEVEL__";
	
	public static void init(ActionContext actionContext){	
		actionContext.g().put(KEY, Executor.getLogLevel());
		Thing self = actionContext.getObject("self");
		
		byte logLevel = self.doAction("getLogLevel", actionContext);
		if(logLevel != -1) {
			Executor.setLogLevel(logLevel);
		}
		
		String executorServiceName = self.getStringBlankAsNull("executorService");
		if(executorServiceName != null) {
			ExecutorService es = getActionContext(actionContext).getObject(executorServiceName);
			if(es != null) {
				actionContext.put("executorService", es);
				
				Executor.push(es);
			}
		}
	}
	
	/**
	 * 当前变量上下文是上下文的变量上下文，获取主变量上下文。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static ActionContext getActionContext(ActionContext actionContext){
		return (ActionContext) actionContext.get(Action.str_acContext);
	}
	

	public static void success(ActionContext actionContext){
		byte logLevel = actionContext.getByte(KEY);
		if(logLevel != -1) {
			Executor.setLogLevel(logLevel);
		}
		
		if(actionContext.get("executorService") != null) {
			Executor.pop();
		}
	}
	
	public static void exception(ActionContext actionContext){
		byte logLevel = actionContext.getByte(KEY);
		if(logLevel != -1) {
			Executor.setLogLevel(logLevel);
		}
		
		if(actionContext.get("executorService") != null) {
			Executor.pop();
		}
	}
		
	public static void onDoAction(ActionContext actionContext){
	}	
}
