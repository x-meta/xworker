package xworker.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import xworker.lang.executor.Executor;

public class TaskExecutor {
	private static final String TAG = "TaskExecutor";
	private static int seq = 0;
	/** TaskExecutor自身的事物 */
    Thing thing;
    
    /** TaskExecutor的变量上下文 */
    ActionContext actionContext;
    
    /** 最后一次执行的任务 */
    Thing currentTask = null;
    
    /** 记录已经结束的任务 */
    Map<String, TaskResult> taskResults = new HashMap<String, TaskResult>();
    
    String id = null;
    
    public TaskExecutor(Thing thing, ActionContext actionContext) {
    	this.thing = thing;
    	this.actionContext = new ActionContext();
    	this.actionContext.put("taskExecutor", this);
    	
    	Map<String, Object> variables = thing.doAction("getVariables", actionContext);
    	if(variables != null) {
    		this.actionContext.putAll(variables);
    	}
    	
    	Bindings bindings = actionContext.peek();
    	Executor.info(TAG, "Bindings value names:");
    	for(String key : bindings.keySet()) {
    		Executor.info(TAG, "    " + key);
    	}
    	
    	seq++;
    	this.id = "TaskExecutor_" + seq + "_" + thing.getMetadata().getPath(); 
    }
   
    private void finish() {
    	if(UtilData.isTrue(thing.doAction("isFinishNotify", actionContext))) {
    		requestUI();
    	}
    }
    
    private void invokeException(Thing task, Exception e) {
    	requestUI();
    }
    
    private boolean checkTaskResult(TaskResult result) {
    	String taskName = result.task.getMetadata().getName();
    	
    	boolean ok = true;
    	String checkAction = "check" + taskName;
    	Thing checkThing = thing.getActionThing(checkAction);
    	if(checkThing != null) {
    		//如果重写了任务动作对应的动作
    		ok = UtilData.isTrue(thing.doAction(checkAction, actionContext, "taskExecutor", this,
    				"task", result.task, "result", result.result));
    	}
    	
    	if(!ok && UtilData.isTrue(thing.doAction("isCheckNotify", actionContext))) {
    		requestUI();
    		
    		return false;
    	}else {
    		if(result.result instanceof TaskExecutor) {
    			TaskExecutor t = (TaskExecutor) result.result;
    			if(t.isFinished()) {
    				result.finished = true;
    				return true;
    			}else {
    				return false;
    			}
    		}else {
	    		result.finished = true;
	    		return true;
    		}
    	}
    }
        
    public boolean isFinished() {
    	Thing tasks = thing.getThing("Tasks");   
    	if(tasks == null || tasks.getChilds().size() == 0) {
    		return true;
    	}
    	
    	for(Thing task : tasks.getChilds()) {
    		String path = task.getMetadata().getPath();
    		TaskResult taskResult = taskResults.get(path);
    		
    		if(taskResult.finished == false) {
    			return false;
    		}
    	}
    	
    	return true;
    }
    
    public void requestUI() {
    	thing.doAction("requestUI", actionContext);
    }
    
    /**
     * 执行指定的子任务，即使已经完成了，还会继续执行。
     * 
     * @param taskThing
     */
    public void execute(Thing taskThing) {
    	TaskResult taskResult = getTaskResult(taskThing);		
		
		//没有结束，需要执行
		try {
			currentTask = taskThing;
			Object result = taskThing.getAction().run(actionContext);
			taskResult.result = result;
			if(!checkTaskResult(taskResult)) {
				//如果没有校验成功，那么会进入UI请求
				return;
			} else {
				taskResult.finished = true;
			}
		}catch(Exception e) {
			taskResult.result = e;
			invokeException(taskThing, e);
		}
    }
    
    public TaskResult getTaskResult(Thing taskThing) {
    	String path = taskThing.getMetadata().getPath();
    	TaskResult taskResult = taskResults.get(path);
		if(taskResult == null) {
			taskResult = new TaskResult();
			taskResult.task = taskThing;
			taskResults.put(path, taskResult);
		}
		
		return taskResult;
    }
    
    /**
     * 执行所有未执行的任务，遇到异常或者检查不通过则停止执行。
     */
    public void execute() {
    	Thing tasks = thing.getThing("Tasks@0");    	
    	if(tasks == null || tasks.getChilds().size() == 0) {
    		finish();
    		return;
    	}
    
    	List<Thing> taskChilds = tasks.getChilds();
    	for(int i =0; i<taskChilds.size(); i++) {
    		Thing taskThing = taskChilds.get(i);
    		TaskResult taskResult = getTaskResult(taskThing);
    		
    		if(taskResult.finished == false) {
    			//没有结束，需要执行
    			try {
    				currentTask = taskThing;
    				Object result = taskThing.getAction().run(actionContext);
    				taskResult.result = result;
    				if(!checkTaskResult(taskResult)) {
    					//如果没有校验成功，那么会进入UI请求
    					return;
    				}else {
    					taskResult.finished = true;
    				}
    			}catch(Exception e) {
    				taskResult.result = e;
    				invokeException(taskThing, e);
    			}
    		}
    	}
    	
    	//全部执行成功，则结束
    	finish();
    }
    
    
    public Thing getThing() {
		return thing;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}

	public Thing getCurrentTask() {
		return currentTask;
	}

	public Map<String, TaskResult> getTaskResults() {
		return taskResults;
	}
	
	public TaskResult getCurrentTaskResult() {
		if(currentTask != null) {
			return taskResults.get(currentTask.getMetadata().getPath());
		}
		
		return null;
	}
	
	public String getLabel() {
		String label = thing.getMetadata().getLabel();
		return label;
	}
	/**
	 * 从Executor上移除本对象产生的UI请求，如果存在的话。
	 */
	public void removeRequest() {
		Executor.removeRequest(thing, actionContext);
	}

	static class TaskResult implements java.lang.Comparable<TaskResult>{
    	public Thing task;
    	public Object result;
    	public boolean finished = false;
    	
		@Override
		public int compareTo(TaskResult o) {
			return task.getMetadata().getName().compareTo(o.task.getMetadata().getName());
		}
    }
	
	public static String getMessageId(ActionContext actionContext) {
		TaskExecutor taskExecutor = actionContext.getObject("taskExecutor");
		return taskExecutor.id;
	}
	
	/**
	 * 在UI请求界面中用户点击了OK按钮。
	 * 
	 * @param actionContext
	 */
	public static void requestOk(ActionContext actionContext) {
		//TaskExecutor taskExecutor = actionContext.getObject("taskExecutor");
		//taskExecutor.execute();
	}
	
	public static Object getLabel(ActionContext actionContext) {
		TaskExecutor taskExecutor = actionContext.getObject("taskExecutor");
		return taskExecutor.getLabel();
	}
	
	public static Object run(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		TaskExecutor taskExecutor = new TaskExecutor(self, actionContext);
		taskExecutor.execute();
		
		return taskExecutor;
	}
}
