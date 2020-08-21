package xworker.app.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;

import xworker.app.init.InitApp;
import xworker.dataObject.DataObject;
import xworker.task.TaskManager;

/**
 * 任务管理器。
 * 
 * @author Administrator
 *
 */
public class AppTaskManager {
	private static Logger logger = LoggerFactory.getLogger(AppTaskManager.class);
	
	/**
	 * 任务缓存。
	 */
	private static Map<String, Thing> taskCache = new HashMap<String, Thing>();
	
	private static String taskDescriptor = "xworker.app.task.TaskDescriptor";
	
  	/**
	 * 刷新任务。
	 * 
	 * @param actionContext
	 */
	@SuppressWarnings("unchecked")
	public synchronized static void refreshTasks(ActionContext actionContext){
		//获取所有计划任务
		if(World.getInstance().getThing("_local.xworker.db.XWorkerAppDataSource") == null){
			logger.info("XWorker app datasource not setted, path=_local.xworker.db.XWorkerAppDataSource");
			return;
		}
		
		Thing taskDataObjectThing = World.getInstance().getThing("xworker.app.task.dataobjects.ScheduledTask");
		List<DataObject> tasks = null;
		try{
			//为防止初始时没有设置数据对象，在这里加入报错日志
			tasks = (List<DataObject>) taskDataObjectThing.doAction("query", actionContext);
			
			if(tasks == null){
				return;
			}
		}catch(Exception e){
			logger.warn("XWorker app task may not property setted, task=" + taskDataObjectThing.getMetadata().getPath(), e);
			return;
		}
		
		Map<String, String> context = new HashMap<String, String>();
		
		for(DataObject taskObj : tasks){
			long lastModified = ((Date) taskObj.get("updateTime")).getTime();
			String thingPath = "__xworker_app_task_" + taskObj.get("id");
			context.put(thingPath, thingPath);
			Thing taskThing = taskCache.get(thingPath);
			if(taskThing == null && UtilData.getBoolean(taskObj.get("status"), false)){
				//任务还没有被启动
				taskThing = new Thing(taskDescriptor);
				taskThing.putAll(taskObj);				
				taskThing.put("extends", taskDescriptor);
				taskThing.put("enable", "true");
				taskThing.put("singleInstance", "true");
				taskThing.put("group", "xworker.app");
				taskThing.put("schedule", "true");
				taskThing.put("taskDataObject", taskObj);
				setTaskThingDesc(taskThing);				
				//taskThing.getMetadata().setPath(thingPath);
				taskThing.getMetadata().setLastModified(lastModified);
				taskThing.doAction("run", actionContext);
				taskCache.put(thingPath, taskThing);
			}else if(taskThing != null && lastModified != taskThing.getMetadata().getLastModified()){
				//任务已经启动但修改了
				if(UtilData.getBoolean(taskObj.get("status"), false) == false){
					//任务已经取消
					TaskManager.remvoeScheduledTask(taskThing);
					taskCache.remove(thingPath);
				}else{
					//任务已经更新，由于是单实例的，因此原则上只要重新运行即可
					taskThing.putAll(taskObj);
					taskThing.doAction("run", actionContext);					
					setTaskThingDesc(taskThing);
					taskThing.getMetadata().setLastModified(lastModified);
				}
			}
		}
		
		//删除已经取消的任务
		List<String> removedKeys = new ArrayList<String>();
		for(String key : taskCache.keySet()){
			if(context.get(key) == null){
				//任务已经从数据库中删除
				Thing taskThing = taskCache.get(key);
				if(taskThing != null){
					TaskManager.remvoeScheduledTask(taskThing);
				}
				removedKeys.add(key);				
			}
		}
		
		for(String key : removedKeys){
			taskCache.remove(key);
		}
		
		//App的初始化任务
		InitApp.run(actionContext);		
	}
	
	private static void setTaskThingDesc(Thing taskThing){
		String actionPath = taskThing.getStringBlankAsNull("actionPath");
		Thing actionThing = World.getInstance().getThing(actionPath);
		if(actionThing != null){
			taskThing.put("label", actionThing.getString("label"));
			String description = actionThing.getMetadata().getDescription();
			/*
			if(description == null){
				description = "";
			}
			description = "<a href=\"javascript:invoke('thing:" + actionPath + "')\">" + actionPath + "</a></p>" +
					description;*/
			taskThing.put("description", description);
		}else {
			String description = "Action not exists, action=" + actionPath;
			taskThing.put("description", description);
		}
		
		String sgroup = taskThing.getStringBlankAsNull("sgroup");
		if(sgroup != null){
			taskThing.put("group", "xworker.app." + sgroup);
		}
	}
	
	/**
	 * Txworker.app.task.TaskDescriptor描述者的doTask方法实现。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Object doTask(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String actionPath = self.getStringBlankAsNull("actionPath");
		try{			
			if(actionPath != null){
				Thing taskThing = World.getInstance().getThing(actionPath);
				if(taskThing != null){
					return taskThing.doAction("run", actionContext);
				}
			}
		}catch(Exception e){
			logger.error("XWorker app task error, id=" + self.get("id") + ", actionPath=" + actionPath, e); 
		}
		
		return null;
	}
}
