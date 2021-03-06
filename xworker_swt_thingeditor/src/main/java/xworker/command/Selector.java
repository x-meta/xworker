package xworker.command;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.xmeta.ActionContext;

import xworker.lang.actions.ActionContainer;

public class Selector {
	public ActionContext actionContext;
	
	public ActionContainer actionContainer;
	
	public Selector(ActionContext actionContext, ActionContainer actionContainer){
		this.actionContainer = actionContainer;
		this.actionContext = actionContext;
	}
	
	public void handleDefaultSelection(Event event){
		handleEvent("select", event);
	}
	
	public void handleKeyEvent(Event event){
		String action = null;
		if(event.keyCode == SWT.UP){
			action = "up";
		}else if(event.keyCode == SWT.DOWN){
			action = "down";
		}else if(event.keyCode == SWT.LEFT){
			action = "left";
		}else if(event.keyCode == SWT.RIGHT){
			action = "right";
		}else if(event.keyCode == SWT.UP){
			action = "up";
		}else if(event.keyCode == SWT.PAGE_DOWN){
			action = "page_down";
		}else if(event.keyCode == SWT.PAGE_UP){
			action = "page_up";
		}else if(event.keyCode == SWT.CR){
			action = "select";
		}
		
		handleEvent(action, event);
	}
	
	private void handleEvent(String name, Event event){
		if(actionContainer == null){
			return;
		}else{
			actionContainer.doAction("handleEvent", actionContext, "action", name, "event", event);
		}		
	}
	
	public void handleSearchEvent(String text){
		if(actionContainer != null){
			actionContainer.doAction("handleEvent", actionContext, "action", "search", "text", text);
		}
	}
}
