package xworker.dataObject.staticdata;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;

public class StaticDataObject {
	public static DataObject getData(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String key = "StaticDataObject";
		DataObject dataObject = self.getStaticData(key);
		if(dataObject == null) {
			dataObject = self.doAction("createDataObject", actionContext);
			
			self.setStaticData(key, dataObject);
		}
		
		return dataObject;
	}
	
	public static DataObject createDataObject(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Thing db = self.doAction("getDataObject", actionContext);
		if(db != null) {
			return new DataObject(db);
		}else {
			return new DataObject();
		}
		
	}
}
