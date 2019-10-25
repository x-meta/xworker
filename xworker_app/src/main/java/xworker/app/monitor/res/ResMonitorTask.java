package xworker.app.monitor.res;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.util.UtilMap;

import xworker.app.monitor.MonitorUtils;
import xworker.dataObject.DataObject;
import xworker.dataObject.utils.DataObjectUtil;

public class ResMonitorTask implements Runnable{
	private static Logger logger = LoggerFactory.getLogger(ResMonitorTask.class);
	
	DataObject monitor;
	long lastModified;
	ActionContext actionContext = new ActionContext();
	long id;
	String name;
	
	public ResMonitorTask(DataObject monitor){
		this.monitor = monitor;
		this.lastModified = ((Date) monitor.get("updateTime")).getTime();
		this.id = (Long) monitor.get("id");
		this.name = (String) monitor.get("name");
		this.actionContext.put("monitor", monitor);		
	}
	
	public void run(){
		try{			
			//logger.info("ResMonitor task running, monitorId=" + monitor.get("id"));
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			MonitorContext context = new MonitorContext(monitor, name + ", id=" + monitor.get("id") + ",time=" + sf.format(new Date()));
			
			//子任务
			List<DataObject> monitorTasks = DataObjectUtil.query("xworker.app.monitor.dataobjects.ResMonitorTaskItems", UtilMap.toMap("monitorId", monitor.get("id"), "status", 1), actionContext);
			for(DataObject mtask : monitorTasks){
				//获取真正的任务定义
				long taskId = mtask.getLong("taskId");
				
				DataObject task = new DataObject("xworker.app.monitor.dataobjects.ResMonitorTasks");
				task.put("id", taskId);
				task = task.load(actionContext);
				
				if(task != null){
					//数据库
					 List<DataObject> resources = DataObjectUtil.query("xworker.app.monitor.dataobjects.ResMonitorResItems",
							 UtilMap.toMap("taskId", mtask.get("id"), "status", 1), actionContext);
					 for(DataObject resource : resources){
						 resource.put(MonitorUtils.resourceTaskKey, task);
					 }
					 
					 ResourceTask dbTask = new ResourceTask(context, actionContext, monitor, mtask, task, resources);
					 MonitorUtils.execute(dbTask);
				}else{
					context.appendContent(task, null, null, "Task not found");
				}
				
				//添加到总任务监控中
				ResMonitor.addMonitorContext(context);
			}
		}catch(Exception e){
			logger.error("Execute DBmonitor error, id=" + id + ", name=" + name, e);
		}
	}
}
