package xworker.ai.agent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.task.TaskManager;

public class Agent implements Runnable{
	/** Agent的属性 */
	public Map<String, Object> attributes = new HashMap<String, Object>();
	
	/** Agent的事物 */
	public Thing thing;
	
	/** 变量上下文，作为环境 */
	public ActionContext actionContext;
	
	/** 定期执行时的时间间隔 */
	public long interval;
	
	Future<?> future;
	
	boolean stopOnNoActions;
	
	public Agent(Thing thing, ActionContext actionContext){
		this.thing = thing; 
		this.actionContext = actionContext;
		
		this.interval = thing.getLong("interval", 0);
		this.stopOnNoActions = thing.getBoolean("stopOnNoActions", false);
	}
		
	@SuppressWarnings("unchecked")
	public void run(){
		List<String> actions = (List<String>) thing.doAction("getActions", actionContext);
		if(actions == null || actions.size() == 0){
			if(stopOnNoActions){
				stop();
			}
		}else{
			for(String action : actions){
				thing.doAction(action, actionContext);
			}
		}
	}
	
	public void start(){
		stop();
		
		if(interval > 0){
			future = TaskManager.getScheduledExecutorService().scheduleAtFixedRate(this, 0, interval, TimeUnit.MILLISECONDS);
		}
	}
	
	public void stop(){
		if(future != null){
			future.cancel(true);
			future = null;
		}
	}
}
