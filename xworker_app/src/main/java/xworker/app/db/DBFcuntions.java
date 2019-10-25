package xworker.app.db;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import xworker.dataObject.DataObject;

public class DBFcuntions {
	public static DataObject getDataSourceByName(ActionContext actionContext){
		Object name = actionContext.get("name");
		if(name instanceof DataObject){
			return (DataObject) name;
		}else{
			String dbName = UtilData.getString(name, null);
			if(dbName == null){
				return null;
			}else{
				DataObject theData = new DataObject("xworker.app.db.dataobject.DataSource");
				theData.put("name", dbName);
				theData = theData.load(actionContext);
				return theData;
			}
		}
	}
	
	public static Thing getDataSourceThing(ActionContext actionContext){
		Object dbDataSource = actionContext.get("dbDataSource");
		if(dbDataSource instanceof DataObject){
			return DBManager.getDataSourceThing((DataObject) dbDataSource);
		}else{
			String name = UtilData.getString(dbDataSource, null);
			if(name == null){
				return null;
			}else{
				return DBManager.getDataSourceThingByName(name, actionContext);
			}
		}
	}
}
