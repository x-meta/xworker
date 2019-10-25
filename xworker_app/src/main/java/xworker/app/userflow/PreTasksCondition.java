package xworker.app.userflow;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import xworker.dataObject.DataObject;

public class PreTasksCondition {
	
	public static boolean and(ActionContext actionContext){
		return isTrue(actionContext, true);
	}
	
	public static boolean or(ActionContext actionContext){
		return isTrue(actionContext, false);
	}
	
	public static boolean isTrue(ActionContext actionContext, boolean and){
		Thing self = actionContext.getObject("self");
		
		List<Thing> tasks = self.doAction("getTaskThings", actionContext);
		if(tasks != null){
			for(Thing taskThing : tasks){
				DataObject task = UserFlowActions.getTask(taskThing, actionContext);
				boolean finished = false;
				if(task == null){
					finished = false;
				}else{
					//判断执行次数
					int finishedCount = self.getInt("finishedCount");
					if(task.getInt("finishedCount") < finishedCount){
						finished = false;
					}else if(task.getBoolean("ingoreCurrentStatus") == false){						
						if(task.getInt("status") > UserTask.RUNNING){
							finished = true;
						}else{
							finished = false;
						}
					}else{
						finished = true;
					}
				}
				
				if(finished && and == false){ //or，任何一个为true，则返回true
					return true;
				}else if(finished == false && and){//and，任何一个为false，总体返回false
					return false;
				}
			}
			//判断子节点
			for(Thing child : self.getChilds()){
				if(UtilData.isTrue(child.doAction("isTrue", actionContext))){
					if(and == false){
						return true;
					}
				}else if(and){
					return false;
				}
			}
			
			//最后总体返回
			if(and){
				return true;
			}else{
				return false;
			}
		}else{
			return true;
		}
	}
}
