package xworker.task.interactive;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class Parameter {
	Thing thing;
	ActionContext actionContext;
	
	InteractiveTask parameterTask;
	InteractiveTask parentTask;
	
	public  Parameter(Thing thing, ActionContext actionContext, InteractiveTask parentTask) {
		this.thing = thing;
		this.actionContext = actionContext;
		this.parentTask = parentTask;
		
		Thing defaultTask = thing.doAction("getDefaultTask", actionContext);
		if(defaultTask != null) {
			parameterTask = new InteractiveTask(defaultTask, actionContext, parentTask);
		}
	}
	
	public boolean isReady() {
		return true;
	}
	
	public boolean isExecuted() {
		return parameterTask != null && parameterTask.executed;
	}
	
	public void execute() {
		
	}
}
