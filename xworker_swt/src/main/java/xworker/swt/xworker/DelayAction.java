package xworker.swt.xworker;

import org.eclipse.swt.widgets.Display;

import xworker.lang.executor.Executor;

public class DelayAction implements Runnable{
	private static final String TAG = DelayAction.class.getName();
	
	private boolean cancel = false;
	private long delay = 2000;
	private Display display;
	private Runnable run;
	private Object lockObj = new Object();
	
	public DelayAction(Display display, long delay){
		this.display = display;
		this.delay = delay;
	}
	
	public DelayAction(Display display, long delay, Runnable run){
		this.display = display;
		this.delay = delay;
		
		setRunnable(run);
	}
	
	public void run(){
		try{
			synchronized(lockObj) {
				lockObj.wait(delay);
				//Thread.sleep(delay);
				
				if(!cancel){
					display.asyncExec(run);
				}
			}
		}catch(Exception e){
			Executor.error(TAG, "DelayAction error", e);
		}
	}
	
	public void setRunnable(Runnable run){
		//先把之前的Cancel
		cancel();
		
		this.cancel = false;
		this.run = run;
		
		new Thread(this).start();
	}
	
	public void cancel(){
		cancel = true;
		
		synchronized(lockObj) {
			lockObj.notify();
		}
	}
	
	
}
