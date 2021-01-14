package xworker.task;

import java.util.concurrent.TimeUnit;

import xworker.lang.executor.Executor;

/**
 * 延迟执行任务。可以频繁的请求任务，但任务不会多次执行，它最多会在指定间隔的频率执行。
 * 
 * @author zyx
 *
 */
public abstract class DelayTask {
	//private static Logger logger = LoggerFactory.getLogger(DelayTask.class);
	private static final String TAG = DelayTask.class.getName();
	
	int interval = 500;
	long lastExecuteTime = 0;
	boolean waittingExecute = false;
	Object lockObj = new Object();
	
	public DelayTask(int interval) {
		this.interval = interval;
	}
	
	public void doTask() {
		synchronized(this) {
			if(waittingExecute) {
				//正在等待执行中
				return;
			}
		}
		
		waittingExecute = true;
		//延迟执行
		TaskManager.getScheduledExecutorService().schedule(new TaskRunnable(this),
				interval, TimeUnit.MILLISECONDS);	
	}
	
	public abstract void run();
	
	static class TaskRunnable implements Runnable{
		DelayTask task;
		long lastExecuteTime;
		
		public TaskRunnable(DelayTask task) {
			this.task = task;
			this.lastExecuteTime = task.lastExecuteTime;
		}
		
		public void run() {
			synchronized(task){
				task.waittingExecute = false;
			}
			synchronized(task.lockObj) {
				try {
					task.run();				
				}catch(Exception e) {
					Executor.error(TAG, "Execute delay task error", e);
				}finally {
					task.lastExecuteTime = System.currentTimeMillis();
					
				}
			}
		}
	}
}
