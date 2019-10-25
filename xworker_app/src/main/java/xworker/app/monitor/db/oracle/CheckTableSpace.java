package xworker.app.monitor.db.oracle;

import java.sql.Connection;

import org.xmeta.ActionContext;

import xworker.app.monitor.db.DbCheckOne;
import xworker.app.monitor.res.MonitorContext;
import xworker.dataObject.DataObject;

public class CheckTableSpace implements DbCheckOne{

	@Override
	public String doCheck(DataObject monitor, DataObject monitorTask,
			DataObject task, DataObject database, DataObject datasource,
			MonitorContext monitorContext, Connection con,
			ActionContext actionContext) {
		return "表空间检测错误";
	}
}
