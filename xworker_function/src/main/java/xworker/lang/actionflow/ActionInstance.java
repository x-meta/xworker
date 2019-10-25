package xworker.lang.actionflow;

import org.xmeta.Thing;

public class ActionInstance {
	Thing actionThing;	
	Object result;
	
	public ActionInstance(Thing actionThing){
		this.actionThing = actionThing;
	}

	public Thing getActionThing() {
		return actionThing;
	}

	public void setActionThing(Thing actionThing) {
		this.actionThing = actionThing;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
	
	
}
