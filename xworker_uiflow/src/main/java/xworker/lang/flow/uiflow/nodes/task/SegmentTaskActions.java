package xworker.lang.flow.uiflow.nodes.task;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.lang.flow.uiflow.ActionFlow;
import xworker.task.segment.SegmentTask;
import xworker.task.segment.SegmentTaskManager;

public class SegmentTaskActions {
	public static void flowRun(ActionContext actionContext){
		//获取节点
		Thing self = actionContext.getObject("self");
		
		SegmentTask task = SegmentTaskManager.getTask(self);
		if(task == null){
			task = new SegmentTask(self, actionContext);
			SegmentTaskManager.putTask(self, task);
			task.start();
		}
	}
	
	public static Object getRanageManager(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Thing rangeManagerCon = null;
		for(Thing child : self.getChilds("Connection")){
			if("RangeManager".equals(child.getMetadata().getName())){
				rangeManagerCon = child;
				break;
			}
		}
		if(rangeManagerCon == null){
			throw new ActionException("SegmentTask must have a RangeManager, path=" + self.getMetadata().getPath());
		}
		
		Thing rangeManager = World.getInstance().getThing(rangeManagerCon.getStringBlankAsNull("nodeRef"));
		if(rangeManager != null){
			//创建RanageManager
			return rangeManager.doAction("run", actionContext);
		}else{
			return null;
		}
	}
	
	public static Object getRangeTaskThing(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Thing callAbleCon = null;
		for(Thing child : self.getChilds("Connection")){
			if("RangeTask".equals(child.getMetadata().getName())){
				callAbleCon = child;
				break;
			}
		}
		Thing callable = World.getInstance().getThing(callAbleCon.getStringBlankAsNull("nodeRef"));
		if(callable != null){
			//创建RanageManager
			Thing taskThing = new Thing("xworker.lang.flow.uiflow.nodes.task.segmentTask.RangeFlowTask");
			taskThing.put("nodeRef", callable.getMetadata().getPath());
			return  taskThing;
		}else{
			return self;
		}
	}
	
	public static Object doRange(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		Thing node = World.getInstance().getThing(self.getStringBlankAsNull("nodeRef"));
		if(node != null){
			//使用新的变量上下文
			ActionContext ac = new ActionContext();
			ac.put("parentContext", actionContext);
			ac.put("task", actionContext.get("task"));
			ac.put("range", actionContext.get("range"));
			ac.put("rangeTask", actionContext.get("rangeTask"));
			ActionFlow actionFlow = new ActionFlow(node, ac);
			actionFlow.start(node);
			
			return ac.get("taskResult");
		}
		
		return false;
	}
}
