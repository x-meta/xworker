package xworker.swt.util;

import java.util.concurrent.TimeUnit;

import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xworker.task.TaskManager;

/**
 * 延迟执行，需要实现doTask方法，当执行execute()方法时，在指定的间隔里doTask方法最多会执行一次。
 * 
 * @author zyx
 *
 */
public abstract class DelayExecutor{
	private static Logger logger = LoggerFactory.getLogger(DelayExecutor.class);
	
	Display display;
	int interval = 500;
	Object lockObj = new Object();
	DelayTask task = null;
	
	/**
	 * 最后真正要执行的方法，在指定间隔里最多触发一次，使用Display.asyncExec()执行。
	 */
	public abstract void doTask();
	
	public DelayExecutor(Display display, int interval) {
		this.display = display;
		this.interval = interval;
	}
	
	private void doRun() {
		if(display == null || display.isDisposed()) {
			return;
		}
		
		display.asyncExec(new Runnable() {
			public void run() {
				try {
					DelayExecutor.this.doTask();
				}catch(Exception e) {
					logger.warn("doTask exception", e);
				}
			}
		});
	}
	
	public void execute() {
		synchronized(lockObj) {
			//延迟执行
			if(task == null || task.finished || task.isTimeOut()){
				task = new DelayTask(this);
				
				TaskManager.getScheduledExecutorService().schedule(task, interval, TimeUnit.MILLISECONDS);
			}
		}
	}
	
	static class DelayTask implements Runnable{
		DelayExecutor executor;
		boolean finished = false;
		long createTime = System.currentTimeMillis();
		
		public DelayTask(DelayExecutor executor) {
			this.executor = executor;
		}
		
		public boolean isTimeOut() {
			return (System.currentTimeMillis() - createTime) > (5 * executor.interval);
		}
		
		public void run(){
			try{
				executor.doRun();
			}catch(Exception e){
				logger.error("execute delay action error", e);
			}finally{
				finished = true;							
			}
		}
	}
}
