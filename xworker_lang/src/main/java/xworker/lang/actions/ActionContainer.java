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

	//动作：xworker.lang.actions.ActionContainer/@actions/@create
	public static ActionContainer create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");

		ActionContainer actionContainer = new ActionContainer(self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), actionContainer);

		return actionContainer;
	}
}
