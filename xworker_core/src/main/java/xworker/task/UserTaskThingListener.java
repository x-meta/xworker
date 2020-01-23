package xworker.task;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class UserTaskThingListener implements UserTaskListener{
	Thing thing;
	ActionContext actionContext;
	
	public UserTaskThingListener(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	@Override
	public void started(UserTask task) {
		thing.doAction("started", actionContext, "task", task);
	}

	@Override
	public void finished(UserTask task) {
		thing.doAction("finished", actionContext, "task", task);
	}

	@Override
	public void progressSetted(UserTask task, int progress) {
		thing.doAction("progressSetted", actionContext, "task", task, "progress", progress);
	}

	@Override
	public void currentLabelUpdated(UserTask task, String label) {
		thing.doAction("currentLabelUpdated", actionContext, "task", task, "label", label);
	}

}
