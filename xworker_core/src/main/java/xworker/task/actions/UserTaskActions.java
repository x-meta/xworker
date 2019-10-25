package xworker.task.actions;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.task.TaskManager;
import xworker.task.UserTask;
import xworker.task.UserTaskManager;
import xworker.util.UtilAction;

public class UserTaskActions {
	private static Logger logger = LoggerFactory.getLogger(UserTaskActions.class);
	
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
						logger.error("Run user task error, path=" + self.getMetadata().getPath(), e);
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
		logger.info("User task doing default task and finished, path=" + self.getMetadata().getPath());		
	}
}
