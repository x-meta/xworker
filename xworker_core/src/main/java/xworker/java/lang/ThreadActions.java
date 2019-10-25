package xworker.java.lang;

import java.util.HashMap;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.executor.Executor;
import xworker.lang.executor.ExecutorService;

public class ThreadActions implements Runnable{
	Thing thing;
	ActionContext actionContext;
	Map<String, Object> params;
	ExecutorService executorService = Executor.getExecutorService();
	
	public ThreadActions(Thing thing, ActionContext actionContext, Map<String, Object> params){
		this.thing = thing;
		this.actionContext = actionContext;
		this.params = params;
	}
	
	public void run(){
		//复制启动该线程的线程的ExecutorService
		if(executorService != null) {
			Executor.push(executorService);
		}
		try {
			if(params != null){
				thing.doAction("doAction", actionContext, params);
			}else{
				thing.doAction("doAction", actionContext);
			}
		}finally {
			Executor.pop();
		}
	}
	
	public static Thread startThread(final ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		
		String variables = self.doAction("getVariables", actionContext);
		Map<String, Object> vars = null;
		if(variables != null){
			vars = new HashMap<String, Object>();
			for(String var : variables.split("[,]")){
				vars.put(var, actionContext.get(var));
			}
		}
		Boolean daemon = self.doAction("isDaemon", actionContext);
		
		Thread thread = new Thread(new ThreadActions(self, actionContext, vars), self.getMetadata().getLabel());
		if(daemon != null && daemon){
			thread.setDaemon(true);
		}
				
		thread.start();
		return thread;
	}
}
