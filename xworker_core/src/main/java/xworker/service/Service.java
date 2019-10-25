package xworker.service;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.ActionContainer;

public class Service {
	Thing thing;
	ActionContainer actions;
	ServiceList serviceList;
	
	public Service(Thing thing, ActionContainer actions) {
		this.thing = thing;
		this.actions = actions;		
	}

	public Thing getThing() {
		return thing;
	}
	
	public ActionContainer getActions() {
		return actions;
	}
	
	public ActionContext getActionContext() {
		return actions.getActionContext();
	}
	
	public void active() {
		if(serviceList != null) {
			serviceList.active(this);
		}
	}
	
	public void remove() {
		if(serviceList != null) {
			serviceList.remove(this);
		}
	}
	
	public <T> T doAction(String name, Object ... parameters) {
		return actions.doAction(name, actions.getActionContext(), parameters);
	}
}
