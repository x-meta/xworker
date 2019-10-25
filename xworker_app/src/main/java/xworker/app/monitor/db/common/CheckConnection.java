package xworker.app.monitor.db.common;

import java.sql.Connection;

import org.xmeta.ActionContext;

import xworker.app.monitor.db.DbCheckOne;
import xworker.app.monitor.res.MonitorContext;
import xworker.dataObject.DataObject;

public class CheckConnection implements DbCheckOne{

	@Override
	public String doCheck(DataObject monitor, DataObject monitorTask,
			DataObject task, DataObject database, DataObject datasource,
			MonitorContext monitorContext, Connection con, ActionContext actionContext) {
		return null;
	}	
}
