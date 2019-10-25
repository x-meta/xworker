package xworker.task;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 延迟执行任务。可以频繁的请求任务，但任务不会多次执行，它最多会在指定间隔的频率执行。
 * 
 * @author zyx
 *
 */
public abstract class DelayTask {
	private static Logger logger = LoggerFactory.getLogger(DelayTask.class);
	
	int interval = 500;
	long lastExecuteTime = 0;
	boolean waittingExecute = false;
	Object lockObj = new Object();
	
	public DelayTask(int interval) {
		this.interval = interval;
	}
	
	public void doTask() {
		synchronized(lockObj) {
			if(waittingExecute) {
				return;
			}else {
				waittingExecute = true;
				long waitTime = System.currentTimeMillis() - lastExecuteTime;
				if(waitTime > interval) {
					//如果大于更新间隔，立即执行
					try {
						run();
					}finally {
						lastExecuteTime = System.currentTimeMillis();
						waittingExecute = false;
					}
				}else {
					//延迟执行
					TaskManager.getScheduledExecutorService().schedule(new Runnable() {
						public void run() {
							synchronized(lockObj) {
								try {
									DelayTask.this.run();
								}catch(Exception e) {
									logger.error("Execute delay task error", e);
								}finally {
									lastExecuteTime = System.currentTimeMillis();
									waittingExecute = false;
								}
							}
						}
					}, interval - waitTime, TimeUnit.MILLISECONDS);
				}
			}
		}
	}
	
	public abstract void run();
}
