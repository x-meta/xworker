package xworker.app.monitor.res;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ExceptionUtil;
import org.xmeta.util.UtilMap;

import xworker.app.monitor.db.DbTask;
import xworker.dataObject.DataObject;

public class ResourceTask extends MonitableTask implements Runnable{		
	private static Logger logger = LoggerFactory.getLogger(DbTask.class);
	
	public List<DataObject> resources;
	
	public ResourceTask(MonitorContext message, ActionContext actionContext, DataObject monitor, DataObject monitorTask, DataObject task, List<DataObject> resources){
		super(message, monitorTask, task, resources, actionContext);
		
		this.resources = resources;
	}
	
	public void doTask(){
		try{			
			Thing taskThing = World.getInstance().getThing(monitorTaskTask.getString("taskPath"));
			if(taskThing == null){
				monitorContext.appendContent(monitorTask, null, null, "TaskThing not exists");
			}else{				
				String result = (String) taskThing.doAction("run", actionContext, UtilMap.toMap("monitor", monitor, "monitorTaskTask", monitorTaskTask, "resources", resources,
						"monitorTask", monitorTask, "monitorContext", monitorContext, "taskThing", taskThing,
						"paramBindings", paramBindings));
				
				if(result != null){
					monitorContext.appendContent(monitorTask, null, null, result);
				}			
			}
		}catch(Exception e){			
			logger.error("Execute dbmonitor dbtask error," +
					"monitorid=" + monitor.getString("id") + ", monitorname=" + monitor.getString("monitor") +
					",taskid=" + monitorTaskTask.get("id") + ",taskname=" + monitorTaskTask.getString("name"), e);
			monitorContext.appendContent(monitorTask, null, null, ExceptionUtil.getRootMessage(e));
		}
	}

}
