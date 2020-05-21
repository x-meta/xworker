package xworker.util;

/**
 * 用于创建一个Runnable，这个Runnable是要在其它线程里执行的，当前线程需要等待结果。
 * 
 * @author zhangyuxiang
 *
 */
public abstract class TheadWaitTask implements Runnable{
	Object lockObj = new Object();
	Object result;
	boolean finished = false;
	
	public void run() {
		synchronized(lockObj) {
			try {
				result = doTask();
			}finally {
				finished = true;
				lockObj.notifyAll();
			}
		}
	}
	
	/**
	 * 执行任务。
	 * @return
	 */
	public abstract Object doTask();
	
	public Object getResult(long timeout) {
		if(finished) {
			return result;
		}
		
		synchronized(lockObj) {
			try {
				lockObj.wait(timeout);
				
				return result;
			} catch (InterruptedException e) {
				return null;
			}
		}
	}
}
