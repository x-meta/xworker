package xworker.lang.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ActionContainer extends org.xmeta.util.ActionContainer{
	public ActionContainer(Thing actions, ActionContext actionContext){
		super(actions, actionContext);
	}
	
	
	public Callback callback(String name){
		return new Callback(this, name);
	}
}
