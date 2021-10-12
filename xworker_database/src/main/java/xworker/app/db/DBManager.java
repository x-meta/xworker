package xworker.app.db;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import xworker.dataObject.DataObject;

public class DBManager {	
	private static Map<String, Thing> dataSourceThings = new HashMap<String, Thing>();
	
	/**
	 * 通过数据源的数据对象获取数据源事物。
	 * 
	 * @param dataObject
	 * @return
	 */
	public static Thing getDataSourceThing(DataObject dataObject){
		String name = (String) dataObject.get("id");
		
		Thing thing = dataSourceThings.get(name);
		Date updateDate = (Date) dataObject.get("updateDate");
		if(thing == null || thing.getMetadata().getLastModified() != updateDate.getTime()){
			if(thing != null){
				//关闭数据源
				thing.doAction("close");
			}
			
			thing = new Thing("xworker.db.jdbc.DataSource");
			thing.putAll(dataObject);
			thing.put("dbType", dataObject.get("dialect"));
			thing.put("type", "c3p0");
			thing.getMetadata().setLastModified(updateDate.getTime());
			
			dataSourceThings.put(name, thing);
		}
		
		return thing;
	}
	
	public static Thing getDataSourceThingByName(String name, ActionContext actionContext){
		String dbName = UtilData.getString(name, null);
		if(dbName == null){
			return null;
		}else{
			DataObject theData = new DataObject("xworker.app.db.dataobject.DataSource");
			theData.put("id", dbName);
			theData = theData.load(actionContext);
			if(theData != null){
				return getDataSourceThing(theData);
			}else{
				return null;
			}
		}
	}
}
