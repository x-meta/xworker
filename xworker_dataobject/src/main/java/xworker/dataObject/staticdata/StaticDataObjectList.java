package xworker.dataObject.staticdata;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.DataObjectList;

public class StaticDataObjectList {
	public static DataObjectList getData(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String key = "StaticDataObjectList";
		DataObjectList dataObjectList = self.getStaticData(key);
		if(dataObjectList == null) {
			dataObjectList = self.doAction("createDataObjectList", actionContext);
			
			self.setStaticData(key, dataObjectList);
		}
		
		return dataObjectList;
	}
	
	public static DataObjectList createDataObjectList(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Thing db = self.doAction("getDataObject", actionContext);
		if(db != null) {
			return  new DataObjectList(db);			
		}else {
			return new DataObjectList();
		}
	}
}
