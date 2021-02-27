package xworker.actions;

import java.util.concurrent.TimeUnit;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import xworker.lang.executor.Executor;
import xworker.task.TaskManager;

public class DelayAction {
	private static final String TAG = DelayAction.class.getName();
	
	public static void run(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Long delay = self.doAction("getDelay", actionContext); 				
		if(delay == null || delay <= 0){
			delay = 1000L;
		}
		synchronized(self){
			String key = "__delayActionTask__";
			DelayTask task = (DelayTask) self.getData(key);
			if(task == null || task.finished || task.isTimeTout()){
				task = new DelayTask();
				task.actionContext = actionContext;
				task.actionThing = self;
				task.vars = new Bindings();
				task.delay = delay;
				String reservedVars = self.doAction("getReservedVars", actionContext);
				if(reservedVars != null){
					for(String var : reservedVars.split("[,]")){
						task.vars.put(var, actionContext.get(var));
					}
				}
				
				TaskManager.getScheduledExecutorService().schedule(task, delay, TimeUnit.MILLISECONDS);
				self.setData(key, task);
			}
		}
		
	}
	
	static class DelayTask implements Runnable{
		Thing actionThing;
		Bindings vars = null;
		ActionContext actionContext;
		boolean finished = false;
		long createTime = System.currentTimeMillis();
		long delay;
				
		public boolean isTimeTout() {
			return (System.currentTimeMillis() - createTime) > (5 * delay); 
		}
		
		public void run(){
			try{
				actionContext.push(vars);
				for(Thing childActions : actionThing.getChilds("actions")){
					for(Thing ac : childActions.getChilds()){
						String name = ac.getMetadata().getName();
						if("getDelay".equals(name) || "getReservedVars".equals(name)){
							continue;
						}
						
						ac.getAction().run(actionContext);
					}
				}
			}catch(Exception e){
				Executor.error(TAG, "execute delay action error, thing=" + actionThing.getMetadata().getPath(), e);
			}finally{
				actionContext.pop();
				finished = true;
			}
		}
	}
}
