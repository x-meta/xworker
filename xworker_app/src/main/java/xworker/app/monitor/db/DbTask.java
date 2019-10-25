package xworker.app.monitor.db;

import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.ExceptionUtil;
import org.xmeta.util.UtilMap;

import xworker.app.monitor.res.MonitableTask;
import xworker.app.monitor.res.MonitorContext;
import xworker.dataObject.DataObject;
import xworker.dataObject.utils.DbUtil;

public  class DbTask extends MonitableTask{		
	private static Logger logger = LoggerFactory.getLogger(DbTask.class);
	
	DataObject monitorTaskResource;
	DataObject monitorTask;
	DataObject datasource;
	Thing taskThing;
	
	public DbTask(MonitorContext message, ActionContext actionContext, DataObject monitor, DataObject monitorTask, DataObject monitorTaskTask, DataObject monitorTaskResource, DataObject datasource, Thing taskThing){
		super(message, monitorTask, monitorTaskTask, monitorTaskResource, actionContext);
		
		this.monitor = monitor;
		this.monitorTaskResource = monitorTaskResource;
		this.monitorTask = monitorTask;
		this.datasource = datasource;
		this.taskThing = taskThing;
	}
	
	public String getDatasourceName(){
		return datasource.getString("name") + "(" + datasource.getString("id") + ")";
	}
	
	public void doTask(){
		Connection con = null;
		try{
			if(datasource == null){
				monitorContext.appendContent(monitorTask, monitorTaskResource, monitorTaskResource.getString("resId"), "Datasource not exists");
				return;
			}
			
			con = (Connection) datasource.doAction("getConnection", actionContext);
			String result = (String) taskThing.doAction("doCheck", actionContext, UtilMap.toMap("monitor", monitor, "monitorTaskTask", monitorTaskTask, "monitorTaskResource", monitorTaskResource,
					"monitorTask", monitorTask, "monitorContext", monitorContext, "datasource", datasource,
					"con", con, "taskThing", taskThing));
			if(result != null){
				monitorContext.appendContent(monitorTask, monitorTaskResource, getDatasourceName(), result);
			}
		}catch(Exception e){			
			logger.error("Execute dbmonitor dbtask error," +
					"monitorId=" + monitor.getString("id") + ", monitorName=" + monitor.getString("monitor") +
					",taskId=" + monitorTask.get("id") + ",taskName=" + monitorTask.getString("taskName"), e);
			
			monitorContext.appendContent(monitorTask, monitorTaskResource, getDatasourceName(), ExceptionUtil.getRootMessage(e));
		}finally{
			if(con != null){
				DbUtil.close(con);
			}
		}
	}
}