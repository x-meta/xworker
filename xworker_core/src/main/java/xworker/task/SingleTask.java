package xworker.task;

import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.executor.Executor;
import xworker.util.UtilAction;

public class SingleTask implements Runnable {
	final static String key = "_SingleTask_"; 
	final static String TAG = SingleTask.class.getName();
	
	Thing thing;
	ActionContext actionContext;
	Map<String, Object> vriables = null;
	
	public SingleTask(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
		vriables = thing.doAction("getVariables", actionContext);
	}
	
	public void run() {		
		try {
			UtilAction.runChildActions(thing.getChilds(), actionContext);
		}catch(Exception e){
			Executor.error(TAG, "SingleTask error: " + thing.getMetadata().getPath());
		}finally {
			thing.setStaticData(key, null);
		}
	}
		
	public static boolean isRunning(ActionContext actionContext) {
		Thing thing = actionContext.getObject("self");
		return thing.getStaticData(key) != null;
	}
	
	public static void run(ActionContext actionContext) {
		Thing thing = actionContext.getObject("self");
		
		synchronized(thing) {
			SingleTask task = thing.getStaticData(key);
			if(task == null) {
				task = new SingleTask(thing, actionContext);
				thing.setStaticData(key, task);
				TaskManager.getExecutorService().execute(task);
			}
		
		}
	}
	
	
}
