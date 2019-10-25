package xworker.app.monitor.db;

import java.sql.Connection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import xworker.app.monitor.MonitorUtils;
import xworker.app.monitor.res.MonitorContext;
import xworker.dataObject.DataObject;
import xworker.dataObject.utils.DataObjectUtil;

public class DbActions {
	private static Logger logger = LoggerFactory.getLogger(DbActions.class);
	
	/**
	 * 每一个资源都当做一个独立的检查任务执行。
	 * 
	 * @param actionContext
	 */
	@SuppressWarnings("unchecked")
	public static void databaseEatchCheck(ActionContext actionContext){
		try{
			DataObject monitor = (DataObject) actionContext.get("monitor");
			DataObject monitorTask = (DataObject) actionContext.get("monitorTask");
			DataObject task = (DataObject) actionContext.get("monitorTaskTask");
			List<DataObject> resources = (List<DataObject>) actionContext.get("resources");
			Thing taskThing = (Thing) actionContext.get("taskThing");
			MonitorContext monitorContext = (MonitorContext) actionContext.get("monitorContext");
			
			for(DataObject resource : resources){
				DataObject datasource = new DataObject("xworker.app.db.dataobject.DataSource");
				datasource.put("id", resource.getString("resId"));
				datasource = datasource.load(actionContext);
				
				DbTask dbTask = new DbTask(monitorContext, actionContext, monitor, monitorTask, task, resource, datasource, taskThing);				
				dbTask.paramBindings = (Bindings) actionContext.get("paramBindings");
				
				MonitorUtils.execute(dbTask);
			}			
		}catch(Exception e){
			logger.error("db check error", e);
		}
	}
	
	public static String dbCheckOne(ActionContext actionContext) throws ClassNotFoundException{
		Thing self = (Thing) actionContext.get("self");
		
		try{
			DataObject monitor = (DataObject) actionContext.get("monitor");
			DataObject monitorTask = (DataObject) actionContext.get("monitorTask");
			DataObject task = (DataObject) actionContext.get("task");
			DataObject database = (DataObject) actionContext.get("database");
			DataObject datasource = (DataObject) actionContext.get("datasource");
			MonitorContext monitorContext = (MonitorContext) actionContext.get("monitorContext");
			Connection con = (Connection) actionContext.get("con");
			
			DbCheckOne checkOne = (DbCheckOne) self.getData("__dbCheckOne__");
			Long lastmodified = (Long) self.getData("__lastModified__");
			if(checkOne == null || lastmodified == null || lastmodified != self.getMetadata().getLastModified()){
				Class<?> cls = Class.forName(self.getString("checkClass"));			
				checkOne = (DbCheckOne) cls.newInstance();
				self.setData("__dbCheckOne__", checkOne);
				self.setData("__lastModified__", self.getMetadata().getLastModified());
			}
			
			return checkOne.doCheck(monitor, monitorTask, task, database, datasource, monitorContext, con, actionContext);
		}catch(Exception e){
			logger.error("db check error", e);
			return e.getMessage();
		}
	}
	
	public static void initTasks(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		DataObject db = new DataObject("xworker.app.monitor.dataobjects.ResMonitorTasks");
		db.put("name", self.getMetadata().getLabel());
		db.put("acceptDialects", self.getString("acceptDialects"));
		db.put("sgoup", self.getString("group"));
		db.put("taskPath", self.getMetadata().getPath());
		
		DataObjectUtil.createIfNotExists(db, new String[]{"taskPath"}, actionContext);
		logger.info("task inited, path=" + self.getMetadata().getPath());
	}
	
	public static Object loadResource(ActionContext actionContext){
		DataObject resource = (DataObject) actionContext.get("resource");
		if(resource != null){
			DataObject datasource = new DataObject("xworker.app.db.dataobject.DataSource");
			datasource.put("id", resource.getString("resId"));
			datasource = datasource.load(actionContext);
			return datasource;
		}
		
		return null;
	}
}
