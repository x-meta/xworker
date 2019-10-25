package xworker.lang.flow.uiflow.nodes.task;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import xworker.lang.flow.uiflow.ActionFlow;
import xworker.lang.flow.uiflow.FlowUtils;
import xworker.lang.flow.uiflow.IFlow;
import xworker.task.Task;
import xworker.task.TaskManager;

public class ScheduleTaskActions {
	private static final Logger logger = LoggerFactory.getLogger(ScheduleTaskActions.class);
	
	public static void runTask(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		IFlow uiFlow = actionContext.getObject("uiFlow");
		
		//作为普通任务执行
		self.doAction("run", actionContext);
		uiFlow.nodeFinished(self, "started");
	}
	
	public static void doTask(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		//IFlow uiFlow = actionContext.getObject("uiFlow");
		
		Thing task  = FlowUtils.getNextFlowNode(self, "doTask");
		if(task != null){
			ActionFlow acFlow = new ActionFlow(task, actionContext);
			acFlow.start(task);
		}else{
			logger.info("Task not setted, flowNode=" + self.getMetadata().getPath());
		}
	}
	
	public static void cancelTask(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		IFlow uiFlow = actionContext.getObject("uiFlow");
		
		Thing taskThing = (Thing) self.doAction("getTaskThing", actionContext);
		if(taskThing != null){
			boolean cancelAll = UtilData.isTrue(self.doAction("isCancelAll", actionContext));
			List<Task> tasks = TaskManager.getTasks(taskThing);
			if(tasks != null){
				if(cancelAll){
					List<Task> list = new ArrayList<Task>();
					list.addAll(tasks);
					for(int i=0; i<list.size(); i++){
						list.get(i).cancel(true);
					}
				}else{
					tasks.get(0).cancel(true);
				}
			}
			
			Task task = TaskManager.getScheduledTask(taskThing);
			if(task != null){
				task.cancel(true);
			}
		}
		
		uiFlow.nodeFinished(self, null);
	}
}
