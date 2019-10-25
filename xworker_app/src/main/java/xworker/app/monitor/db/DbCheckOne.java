package xworker.app.monitor.db;

import java.sql.Connection;

import org.xmeta.ActionContext;

import xworker.app.monitor.res.MonitorContext;
import xworker.dataObject.DataObject;

/**
 * 检查监控一个数据库的接口。
 * 
 * @author Administrator
 *
 */
public interface DbCheckOne {
	/**
	 * 执行检查和监控，如果异常那么返回相应的文字，否则返回null。
	 * 
	 * @param monitor   监控总定义，见事物：xworker.app.monitor.dataobjects.DbMonitor
	 * @param monitorTask 监控任务定义，见事物：xworker.app.monitor.dataobjects.DbMonitorTasks
	 * @param task 数据库任务定义，见事物：xworker.app.monitor.dataobjects.DbTask
	 * @param database 数据库定义，见事物：xworker.app.monitor.dataobjects.DbMonitorDbs
	 * @param datasource 数据源，见事物：xworker.app.db.dataobject.DataSource
	 * @param monitorContext 监控上下文
	 * @param actionContext 变量上下文
	 * 
	 * @return 如果异常那么返回相应的文字，否则返回null。
	 */
	public String doCheck(DataObject monitor, DataObject monitorTask, DataObject task, DataObject database,
			DataObject datasource,  MonitorContext monitorContext, Connection con, ActionContext actionContext);
}
