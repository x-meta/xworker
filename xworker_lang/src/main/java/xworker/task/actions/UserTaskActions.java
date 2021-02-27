package xworker.task.actions;

import java.util.HashMap;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.executor.Executor;
import xworker.task.TaskManager;
import xworker.task.UserTask;
import xworker.task.UserTaskManager;
import xworker.task.UserTaskThingListener;
import xworker.util.UtilAction;

public class UserTaskActions {
	//private static Logger logger = LoggerFactory.getLogger(UserTaskActions.class);
	private static final String TAG = UserTaskActions.class.getName();
	
	public static Object run(final ActionContext actionContext){
		final Thing self = (Thing) actionContext.get("self");
		
		final UserTask task = UserTaskManager.createTask(self, self.getBoolean("progressAble"));
		task.setActionContext(actionContext);
		task.start();
		
		final Map<String, Object> params = UtilAction.getChildChildsResultMap(self, "Variables", actionContext);
		params.put("task", task);
		if(self.getBoolean("async")){
			TaskManager.getExecutorService().execute(new Runnable(){
				public void run(){
					try{
						self.doAction("doTask", actionContext, params);
					}catch(Exception e){
						Executor.error(TAG, "Run user task error, path=" + self.getMetadata().getPath(), e);
					}
				}
			});
			
			return null;
		}else{
			return self.doAction("doTask", actionContext, params);			
		}
	}
	
	public static void doTask(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		UserTask task = (UserTask) actionContext.get("task");
		task.finished();
		Executor.info(TAG, "User task doing default task and finished, path=" + self.getMetadata().getPath());		
	}
	
	/**
	 * 新的UserTask动作实现，路径xworker.lang.task.UserTask。
	 * 
	 * @param actionContext
	 */
	public static void runUserTask(final ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String varName = self.doAction("getUserTaskVarName", actionContext);
		String executeType = self.doAction("getExecuteType", actionContext);
		Map<String, Object> variables = self.doAction("getVariables", actionContext);
		if(variables == null) {
			variables = new HashMap<String, Object>();
		}		
		
		final UserTask task = UserTaskManager.createTask(self, !self.getBoolean("INDETERMINATE"));
		task.setLabel(self.getMetadata().getLabel());
		task.setDetail(self.getMetadata().getDescription());
		variables.put(varName, task);
		final Map<String, Object> params = variables;
		
		//监听器
		for(Thing listener : self.getChilds("UserTaskListener")) {
			UserTaskThingListener lis = new UserTaskThingListener(listener, actionContext);
			task.addListener(lis);
		}
		
		//执行
		if("executor".equals(executeType)) {
			TaskManager.getExecutorService().execute(new Runnable(){
				public void run(){
					try{
						executeUserTask(task, params, actionContext);
					}catch(Exception e){
						Executor.error(TAG, "Run user task error, path=" + self.getMetadata().getPath(), e);
					}
				}
			});
		}else if("thread".equals(executeType)) {
			new Thread(new Runnable() {
				public void run() {
					try{
						executeUserTask(task, params, actionContext);
					}catch(Exception e){
						Executor.error(TAG, "Run user task error, path=" + self.getMetadata().getPath(), e);
					}
				}
			}).start();
		}else {
			executeUserTask(task, params, actionContext);
		}
	}
	
	public static void executeUserTask(UserTask task, Map<String, Object> variables, ActionContext actionContext) {
		try {
			task.setActionContext(actionContext);
			task.start();
			
			Thing self = task.getTaskThing();
			for(Thing child : self.getChilds()) {
				if(task.isStoped()) {
					break;
				}
				
				child.getAction().run(actionContext, variables, true);
				
				if(ActionContext.RETURN == actionContext.getStatus() || 
		                ActionContext.CANCEL == actionContext.getStatus() || 
		                ActionContext.BREAK == actionContext.getStatus() || 
		                ActionContext.EXCEPTION == actionContext.getStatus() ||
		                ActionContext.CONTINUE == actionContext.getStatus()){
	                break;
	            }
			}
		}finally {
			//UserTask里的中断只针对UserTask，所以要重新设定为RUNNING
			actionContext.setStatus(ActionContext.RUNNING);
			task.finished();
		}
	}
	
	private static UserTask getUserTask(Thing child, ActionContext actionContext) {
		Thing taskThing = child.getParent();
		String varName = taskThing.doAction("getUserTaskVarName", actionContext);
		UserTask userTask = actionContext.getObject(varName);
		return userTask;
	}
	
	public static void setTotalJobs(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		UserTask task = getUserTask(self, actionContext);
		int totalJobs = self.doAction("getTotalJobs", actionContext);
		task.setTotalJobs(totalJobs);
	}
	
	public static void setLabelDetail(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		UserTask task = getUserTask(self, actionContext);
		task.setCurrentLabel(self.getMetadata().getLabel());
		
		String detail = self.getMetadata().getDescription();
		if(detail == null || "".equals(detail)) {
			if(self.getBoolean("ignoreBlankDescription")) {
				return;
			}
		}
		
		task.setDetail(detail);
	}
	
	public static void appendCompleteJobs(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		UserTask task = getUserTask(self, actionContext);
		int completeJobs = self.doAction("getCompleteJobs", actionContext);
		task.completeJobs(completeJobs);
	}
	
	public static void cancel(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		UserTask task = getUserTask(self, actionContext);
		task.cancel();
	}
}
