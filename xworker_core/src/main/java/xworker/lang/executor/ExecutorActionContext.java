package xworker.lang.executor;

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
	}
	

	public static void success(ActionContext actionContext){
		byte logLevel = actionContext.getByte(KEY);
		if(logLevel != -1) {
			Executor.setLogLevel(logLevel);
		}
	}
	
	public static void exception(ActionContext actionContext){
		byte logLevel = actionContext.getByte(KEY);
		if(logLevel != -1) {
			Executor.setLogLevel(logLevel);
		}
	}
		
	public static void onDoAction(ActionContext actionContext){
	}	
}
