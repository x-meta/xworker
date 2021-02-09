package xworker.swt.model;

import java.util.Collection;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectList;
import xworker.lang.executor.Executor;

public class DataModelActions {
	private static final String TAG = DataModelActions.class.getName();
	
	public static void createDataObject(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Thing dataObject = self.doAction("getDataObject", actionContext);
		if(dataObject == null) {
			Executor.warn(TAG, "Can not create dataobject, getDataObject() return null, path=" + self.getMetadata().getPath());
			return;
		}
		
		DataObject data = new DataObject(dataObject);
		
		Object objectForWrapper = self.doAction("getObjectForWrapper", actionContext);
		String attributes = self.doAction("getAttributes", actionContext);
		
		if(attributes != null) {
			data.setAttributes(attributes, actionContext);
		}
		
		if(objectForWrapper != null) {
			data.setWrappedObject(objectForWrapper);
		}
		
		actionContext.g().put(self.getMetadata().getName(), data);
	}
	
	public static void createDataObjectList(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Thing dataObject = self.doAction("getDataObject", actionContext);
		if(dataObject == null) {
			Executor.warn(TAG, "Can not create dataobject, getDataObject() return null, path=" + self.getMetadata().getPath());
			return;
		}
		
		DataObjectList dataList = new DataObjectList(dataObject);
		
		Collection<?> collections = self.doAction("getCollections", actionContext);
		if(collections != null) {
			for(Object obj : collections) {
				if(obj instanceof DataObject) {
					dataList.add((DataObject) obj);
				} else {
					DataObject data = new DataObject(dataObject);
					if(obj != null) {
						data.setWrappedObject(obj);
					}
					dataList.add(data);
				}
			}
		}		
		
		actionContext.g().put(self.getMetadata().getName(), dataList);
	}
}
