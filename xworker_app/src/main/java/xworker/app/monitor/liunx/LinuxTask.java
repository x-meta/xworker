package xworker.app.monitor.liunx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ExceptionUtil;
import org.xmeta.util.UtilMap;

import xworker.app.monitor.res.MonitableTask;
import xworker.app.monitor.res.MonitorContext;
import xworker.dataObject.DataObject;

public class LinuxTask extends MonitableTask{		
	private static Logger logger = LoggerFactory.getLogger(LinuxTask.class);
	
	DataObject monitorTaskResource;
	Object server;
	Thing taskThing;
	
	public LinuxTask(MonitorContext message, ActionContext actionContext, DataObject monitor, DataObject monitorTask, DataObject monitorTaskTask, DataObject monitorTaskResource, Object server, Thing taskThing){
		super(message, monitorTask, monitorTaskTask, monitorTaskResource, actionContext);
		
		this.monitorTaskResource = monitorTaskResource;
		this.server = server;
		this.taskThing = taskThing;
	}
	
	public String getServerName(){
		return monitorTaskResource.getString("name") + "(" + monitorTaskResource.getString("id") + ")";
	}
	
	public void doTask(){
		try{
			if(server == null){
				monitorContext.appendContent(monitorTask, monitorTaskResource, getServerName(), "Server not exists");
				return;
			}
			
			Action exec = World.getInstance().getAction("xworker.app.monitor.tasks.linux.ExecuteCommandWithServer");
			String command = (String) taskThing.doAction("getCommand", actionContext, 
					UtilMap.toMap("monitor", monitor, 
							"monitorTask", monitorTask, "resourceTask", monitorTaskTask, 
							"monitorTaskResource", monitorTaskResource, "server", server, 
							"monitorContext", monitorContext));
			if(command == null){
				monitorContext.appendContent(monitorTask, monitorTaskResource, getServerName(), "Command is null");
				return;
			}

			String result = (String) exec.run(actionContext, UtilMap.toMap("server", server, "command", command));

			result = (String) taskThing.doAction("doCheck", actionContext, UtilMap.toMap("monitor", monitor, 
					"monitorTask", monitorTask, "resourceTask", monitorTaskTask, 
					"monitorTaskResource", monitorTaskResource, "server", server, "result", result,
					"monitorContext", monitorContext, "exception", null));
			if(result != null){
				monitorContext.appendContent(monitorTask, monitorTaskResource, getServerName(), result);
			}
		}catch(Exception e){			
			logger.error("Execute linux task error," +
					"monitorid=" + monitor.getString("id") + ", monitorname=" + monitor.getString("monitor") +
					",taskid=" + monitorTask.get("id") + ",taskname=" + monitorTask.getString("name"), e);
			monitorContext.appendContent(monitorTask, monitorTaskResource, getServerName(), ExceptionUtil.getRootMessage(e));
			
			String result = (String) taskThing.doAction("doCheck", actionContext, UtilMap.toMap("monitor", monitor, 
					"monitorTask", monitorTask, "resourceTask", monitorTaskTask, 
					"monitorTaskResource", monitorTaskResource, "server", server, "result", null,
					"monitorContext", monitorContext, "exception", null));
			if(result != null){
				monitorContext.appendContent(monitorTask, monitorTaskResource, getServerName(), result);
			}
			
		}
	}

}
