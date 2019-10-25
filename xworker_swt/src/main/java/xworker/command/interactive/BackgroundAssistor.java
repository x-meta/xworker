package xworker.command.interactive;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于InteractiveUI的后台辅助程序。
 * 
 * @author zyx
 *
 */
public class BackgroundAssistor implements Runnable, Listener{
	private static Logger logger = LoggerFactory.getLogger(BackgroundAssistor.class);
	
	/** 主要监听鼠标和键盘事件，用来判断用户的状态 */
	private static int[] eventTypes = {
		SWT.MouseMove, SWT.MouseDown, SWT.MouseHorizontalWheel, SWT.MouseVerticalWheel, SWT.KeyDown
	};
	
	Display display = null;	
	boolean stoped = false;
	long lastEventTime = 0;
	boolean idle = false;
	long idleInterval = 5000;
	Event lastEvent = null;
	
	public BackgroundAssistor(Display display){
		this.display = display;
		this.display.addListener(SWT.Dispose, new Listener(){
			@Override
			public void handleEvent(Event event) {
				stoped = true;
			}			
		});
		
		for(int eventType : eventTypes){
			this.display.addFilter(eventType, this);
		}
		
		new Thread(this, "BackgroundAssistor").start();
	}
	
	public void idled(){
		if(!idle){
			idle = true;
		}
	}
	
	public void run(){
		logger.info("BackgroundAssistor started");
		while(true){
			if(stoped){
				break;
			}
						
			try {
				if(lastEventTime != 0 && System.currentTimeMillis() - lastEventTime > idleInterval){
					idled();
				}else{
					idle = false;
				}
				
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				break;
			}
		}
		
		logger.info("BackgroundAssistor stoped");
	}

	@Override
	public void handleEvent(Event event) {
		lastEventTime = System.currentTimeMillis();
		lastEvent = event;
	}

}
