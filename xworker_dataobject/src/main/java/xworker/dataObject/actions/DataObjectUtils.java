package xworker.dataObject.actions;

import java.io.IOException;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilAction;

import ognl.OgnlException;
import xworker.dataObject.DataObject;
import xworker.lang.actions.ActionUtils;

public class DataObjectUtils {
	public static void initThings(ActionContext actionContext) throws Exception {
		Thing self = actionContext.getObject("self");				
		Thing configThing = (Thing) self.doAction("getConfigThing", actionContext);
				
		Thing dataObject = null;
		Thing formThing = null;
		Thing condition = null;
		if(self.getBoolean("initDataObject")){ 
			dataObject = ActionUtils.getThing(self, configThing, "dataObjectAttrName", "dataObjectChildName", true, true, actionContext);
			
			UtilAction.putVarByActioScope(self, self.getString("dataObjectVarName"), dataObject, actionContext);
		}

		if(self.getBoolean("initFormDataObject")){
			formThing = ActionUtils.getThing(self, configThing, "formDataObjectAttrName", "formDataObjectChildName", true, false, actionContext);
			//没有从数据对象上取
			if(dataObject != null && formThing == null){
				formThing = ActionUtils.getThing(dataObject, "QueryFormDataObject", true);
			}
			

			UtilAction.putVarByActioScope(self, self.getString("formDataObjectVarName"), formThing, actionContext);
		}
		
		if(self.getBoolean("initCondition")){
			condition = ActionUtils.getThing(dataObject, configThing, "conditionAttrName", "conditionChildName", true, false, actionContext);
			//没有则从数据对象上取
			if(dataObject != null && condition == null){
				condition = ActionUtils.getThing(dataObject, "Condition", false);
			}
			
			UtilAction.putVarByActioScope(self, self.getString("conditionVarName"), condition, actionContext);
		}
	}
	
	public static Object max(ActionContext actionContext){
		return getMaxMin(actionContext, true, true);
	}
	
	public static Object getValue(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		DataObject data = actionContext.getObject("theData");
		String fieldName = (String) self.doAction("getFieldName", actionContext);
		return data.get(fieldName);
	}
	
	public static Object maxValue(ActionContext actionContext){
		return getMaxMin(actionContext, true, false);
	}
	
	public static Object min(ActionContext actionContext){
		return getMaxMin(actionContext, false, true);
	}
	
	public static Object minValue(ActionContext actionContext){
		return getMaxMin(actionContext, false, false);
	}
	
	@SuppressWarnings("unchecked")
	public static Object getMaxMin(ActionContext actionContext, boolean max, boolean returnDataObject){
		Thing self = actionContext.getObject("self");
		
		List<DataObject> datas = (List<DataObject>) self.doAction("getDataObjects", actionContext);
		if(datas == null || datas.size() == 0){
			return null;
		}
		
		DataObject maxMinData = datas.get(0);
		Comparable<Object> maxMinValue = (Comparable<Object>) self.doAction("getValue", actionContext, "theData", maxMinData);
		
		for(int i=1; i<datas.size(); i++){
			DataObject data = datas.get(i);
			Comparable<Object> value = (Comparable<Object>) self.doAction("getValue", actionContext, "theData", data);
			if(maxMinValue == null){
				if(value != null && max){
					maxMinData = data;
					maxMinValue = value;
					continue;
				}else{
					continue;
				}
			}else if(value == null){
				if(maxMinValue != null && !max){
					maxMinData = data;
					maxMinValue = value;
					continue;
				}else{
					continue;
				}
			}else if(max && maxMinValue.compareTo(value) < 0){
				maxMinData = data;
				maxMinValue = value;
			}else if(!max &&  maxMinValue.compareTo(value) < 0){
				maxMinData = data;
				maxMinValue = value;
			}
		}
		
		if(returnDataObject){
			return maxMinData;
		}else{
			return maxMinValue;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static double avg(ActionContext actionContext, boolean max, boolean returnDataObject){
		Thing self = actionContext.getObject("self");
		
		List<DataObject> datas = (List<DataObject>) self.doAction("getDataObjects", actionContext);
		if(datas == null || datas.size() == 0){
			throw new ActionException("DataObject list is null or size=0, action=" + self.getMetadata().getPath());
		}
		
		double total = 0;
		String fieldName = (String) self.doAction("getFieldName", actionContext);
		for(DataObject data : datas){
			total += data.getDouble(fieldName);
		}
		
		return total / datas.size();
	}
}
