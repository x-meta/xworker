package xworker.dataObject.staticdata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectList;
import xworker.util.UtilData;

public class StaticDataActions {
	public static Object getDataObjectList(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Thing thing = self.doAction("getStaticDataObjectList", actionContext);
		if(thing != null) {
			return thing.doAction("getData", actionContext);
		}else {
			return null;
		}
	}
	
	public static Object getDataObject(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Thing thing = self.doAction("getStaticDataObject", actionContext);
		if(thing != null) {
			return thing.doAction("getData", actionContext);
		}else {
			return null;
		}
	}
	
	public static Object getDataObjectFormList(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		DataObjectList list = (DataObjectList) getDataObjectList(actionContext);
		if(list == null) {
			return null;
		}
		
		Map<String, Object> keyValues = self.doAction("getKeyValues", actionContext);
		Object[] keys = new Object[keyValues.size() * 2];
		int index=0;
		List<String> keyNames = new ArrayList<String>();
		for(String key : keyValues.keySet()) {
			keys[index] = key;
			keys[index + 1] = keyValues.get(key);
			keyNames.add(key);
			
			index = index + 2;
		}
		
		DataObject data = list.get(keys);
		if(data == null && self.getBoolean("createIfNotExists")) {
			Map<String, Object> attributes = self.doAction("getAttributes", actionContext);
			data = new DataObject(list.getDescriptor());
			if(list.getDescriptor() != null) {
				data.init(actionContext);
			}
			data.putAll(keyValues);
			if(attributes != null) {
				data.putAll(attributes);
			}
			
			//执行创建
			list.add(data);
			data.createIfNotExists(keyNames, actionContext);			
		}else if(UtilData.isTrue(self.doAction("isUpdate", actionContext))) {
			Map<String, Object> attributes = self.doAction("getAttributes", actionContext);
			if(attributes != null) {
				data.putAll(attributes);
			}
		}
		
		return data;
	}
	
	public static Object addDataObjectToList(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		DataObjectList list = (DataObjectList) getDataObjectList(actionContext);
		if(list == null) {
			return null;
		}
		
		DataObject dataObject = self.doAction("getDataObject", actionContext);
		if(dataObject == null) {
			return null;
		}
		
		//是否已存在
		if(list.contains(dataObject)) {
			return dataObject;
		}
		
		//是否相同主键的已存在
		Object[][] keyDatas = dataObject.getKeyAndDatas();
		Object[] keys = new Object[keyDatas.length * 2];
		
		for(int i=0; i<keyDatas.length; i++) {
			keys[2 * i] = keyDatas[i][0];
			keys[2 * i + 1] = keyDatas[i][1];
		}
		
		if(list.get(keys) == null) {
			list.add(dataObject);
		}
		
		return dataObject;
	}
}
