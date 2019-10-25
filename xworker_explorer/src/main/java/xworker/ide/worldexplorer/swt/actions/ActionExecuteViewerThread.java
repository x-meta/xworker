package xworker.ide.worldexplorer.swt.actions;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Button;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilString;

import xworker.lang.actions.ActionContainer;

public class ActionExecuteViewerThread implements Runnable{
	ActionContext actionContext;
	ActionContext actionThingContext;
	boolean running;
	Button startStopButton;
	Button nextButton;
	private Object lockObj = new Object();
	ActionContainer actions;
	Thing startActionThing;
	Thread thread;
	
	public ActionExecuteViewerThread(ActionContext actionContext){
		this.actionContext = actionContext;
		startStopButton = actionContext.getObject("startStopButton");
		nextButton = actionContext.getObject("nextButton");
		nextButton.addDisposeListener(new DisposeListener(){
			@Override
			public void widgetDisposed(DisposeEvent event) {
				stop();
			}
			
		});
		actions = actionContext.getObject("actions");
	}
	
	public void checkStatus(){
		Thing actionThing = actionContext.getObject("actionThing");
		
		if(!running){
			startStopButton.setText(UtilString.getString("lang:d=启动&en=Start", null));
			if(actionThing != null){
				startStopButton.setEnabled(true);
			}else{
				startStopButton.setEnabled(false);
			}
			nextButton.setEnabled(false);
		}else{
			nextButton.setEnabled(true);
			
			startStopButton.setText(UtilString.getString("lang:d=停止&en=Stop", null));
		}
	}
	
	public static void next(ActionContext actionContext){
		ActionExecuteViewerThread executeThread = actionContext.getObject("executeThread");
		executeThread.next();
	}
	
	public void next(){
		synchronized(lockObj){
			lockObj.notify();
		}
	}
	
	public boolean isActionThing(Thing thing){
		Thing actionThing = actionContext.getObject("actionThing");
		return actionThing.getRoot() == thing.getRoot();
	}
	
	public void showContext(Thing thing){
		if(!isActionThing(thing)){
			return;
		}
		
		actions.doAction("showInfo", actionContext, "thing", thing);
		try {
			synchronized(lockObj){
				lockObj.wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void start(Thing startActionThing){
		if(!running){
			this.startActionThing = startActionThing;
			
			thread = new Thread(this);
			thread.start();		
		}
	}
	
	public void stop(){
		synchronized(lockObj){
			running = false;
			lockObj.notify();
		}
	}
	
	public void run(){
		running = true;
		actionThingContext = actionContext.getObject("actionThingContext");
		try{
			Bindings bindings = actionThingContext.push();
			bindings.put("actionExecutor", this);
			bindings.setContextThing(World.getInstance().getThing("xworker.ide.worldExplorer.swt.actions.ActionExecuteViewerContext"));
			if(startActionThing != null){
				final Object result = startActionThing.doAction("run", actionThingContext);
				nextButton.getDisplay().asyncExec(new Runnable(){
					public void run(){
						actions.doAction("setResult", actionContext, "result", result);
					}
				});
				
			}
			
			
		}catch(final Exception e){
			nextButton.getDisplay().asyncExec(new Runnable(){
				public void run(){
					actions.doAction("setResult", actionContext, "result", e);
				}
			});
		}finally{
			running = false;
			actionThingContext.pop();
			if(nextButton.isDisposed() == false){
				nextButton.getDisplay().asyncExec(new Runnable(){
					public void run(){
						checkStatus();
					}
				});
			}
			
		}
	}
}
