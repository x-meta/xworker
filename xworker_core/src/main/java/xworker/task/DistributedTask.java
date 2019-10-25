package xworker.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

/**
 * 分布式任务。
 * 
 * @author Administrator
 *
 */
public class DistributedTask implements Runnable{
	Thing thing;
	ActionContext actionContext;
	
	boolean finished = false;
	List<RunTask> tasks = new ArrayList<RunTask>();
	ScheduledFuture<?> monitorFuture = null;
	long timeOut = 0;
	
	public DistributedTask(Thing thing, ActionContext actionContext){
		this.thing = thing;
		this.actionContext = actionContext;
		
		timeOut = thing.getLong("timeOut");
		thing.doAction("init", actionContext, UtilMap.toMap("task", this));
		
		monitorFuture = TaskManager.getScheduledExecutorService().schedule(this, 300, TimeUnit.MILLISECONDS);
	}
	
	public void cancel(){
		finished = false;			
	}
	
	public void startTasks(List<Runnable> tasks){
		for(Runnable task : tasks){
			RunTask t = new RunTask(this, task);
			t.future = TaskManager.getExecutorService().submit(t, task);
			this.tasks.add(t);
		}
	}
	
	public void taskFinished(Runnable task){
		thing.doAction("taskFinished", actionContext, UtilMap.toMap("task", this, "childTask", task));
	}
	
	public void checkFinished(){
		if(tasks.size() == 0){
			finished = true;
		}
	}
	
	public void run(){
		if(finished){
			//任务已经被取消或先前已终止，终止自己
			monitorFuture.cancel(true);
			return;
		}
		
		for(int i=0; i<tasks.size(); i++){
			RunTask task = tasks.get(i);
			if(task.future.isDone() || task.future.isCancelled()){
				tasks.remove(i);
				i--;
			}else if(timeOut > 0 && System.currentTimeMillis() - task.startTime > timeOut){
				task.cacnel();
				
				thing.doAction("timeout", actionContext, UtilMap.toMap("task", this, "childTask", task));
				tasks.remove(i);
				i--;
			}
		}
		
		if(tasks.size() == 0){
			finished = true;
			
			//任务已经完成，终止自己
			monitorFuture.cancel(true);
			
			thing.doAction("finished", actionContext);
		}
	}
	
	public static Object taskRun(ActionContext actionContext){
		Thing thing = (Thing) actionContext.get("self");
		
		return new DistributedTask(thing, actionContext);
	}
	
	public static class RunTask implements Runnable{
		DistributedTask task;
		Runnable run;
		Future<?> future;
		boolean canceled = false;
		long startTime = System.currentTimeMillis();
		
		public RunTask(DistributedTask task, Runnable run){
			this.task = task;
			this.run = run;
		}
		
		public void cacnel(){
			this.canceled = true;
			future.cancel(true);
		}
		public void run(){
			run.run();
			
			if(!canceled){
				task.taskFinished(run);
			}
		}
	}
}
