package xworker.ui.dataObject;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import xworker.dataObject.DataObject;
import xworker.dataObject.PageInfo;
import xworker.dataObject.utils.DataObjectUtil;
import xworker.lang.function.xworker.ThingFunctions;
import xworker.ui.function.FunctionRequest;

public class DataObjectFunctions {
	/**
	 * 数据对象记录转相应的函数。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Thing toGetRecordFunction(ActionContext actionContext){
		String name = (String) actionContext.get("name");
		Object value = actionContext.get("value");
		if(value == null){
			Thing thing = new Thing("xworker.lang.function.values.NullValue");
			thing.put("name", name);
			
			return thing;
		}else if(value instanceof DataObject){
			DataObject data = (DataObject) value;
			Thing descriptor = data.getMetadata().getDescriptor();
			
			Thing thing = new Thing("xworker.ui.dataObject.GetRecordById");
			thing.put("name", name);
			
			//参数 dataObject
			Thing dataObject = new Thing("xworker.lang.function.values.StringValue");
			dataObject.put("name", "dataObject");
			dataObject.put("value", descriptor.getMetadata().getPath());
			thing.addChild(dataObject);
			
			//参数id
			Thing id = new Thing("xworker.lang.function.values.StringValue");
			id.put("name", "id");
			id.put("value", String.valueOf(data.getKeyAndDatas()[0][1]));
			thing.addChild(id);
			
			return thing;
		}else if(value instanceof List){
			List<DataObject> datas = (List<DataObject>) value;
			if(datas.size() == 0){
				//空的列表
				Thing thing = new Thing("xworker.lang.function.values.NewList");
				thing.put("name", name);
				return thing;
			}else{
				Thing descriptor = datas.get(0).getMetadata().getDescriptor();
				
				Thing thing = new Thing("xworker.ui.dataObject.GetRecordsByIds");
				thing.put("name", name);
				
				//参数 dataObject
				Thing dataObject = new Thing("xworker.lang.function.values.StringValue");
				dataObject.put("name", "dataObject");
				dataObject.put("value", descriptor.getMetadata().getPath());
				thing.addChild(dataObject);
				
				//参数id
				Thing id = new Thing("xworker.lang.function.values.StringValue");
				id.put("name", "id");
				
				String idValue = null;
				for(DataObject data : datas){
					if(idValue == null){
						idValue = String.valueOf(data.getKeyAndDatas()[0][1]);
					}else{
						idValue = idValue + "," + String.valueOf(data.getKeyAndDatas()[0][1]);
					}
				}
				id.put("value", idValue);
				thing.addChild(id);
				
				return thing;
			}
		}else{
			Thing thing = new Thing("xworker.lang.function.values.NullValue");
			thing.put("name", name);
			
			return thing;
		}
	}
	
	/**
	 * 装载数据对象。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static DataObject loadDataObject(ActionContext actionContext){
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		Object dataObj = request.getParameter("dataObject").getValue();
		if(dataObj instanceof DataObject){
			DataObject dataObject = (DataObject) dataObj;
			return dataObject.load(actionContext);
		}else{
			throw new ActionException("loadDataObject： not a DataObject, it is a " + dataObj, actionContext);
		}
	}
	
	/**
	 * 更新数据对象，返回更新的记录数。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static int updateDataObject(ActionContext actionContext){
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		Object dataObj = request.getParameter("dataObject").getValue();
		if(dataObj instanceof DataObject){
			DataObject dataObject = (DataObject) dataObj;
			return dataObject.update(actionContext);
		}else{
			throw new ActionException("updateDataObject： not a DataObject, it is a " + dataObj, actionContext);
		}
	}
	
	/**
	 * 删除数据对象，返回删除的记录数量。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static int deleteDataObject(ActionContext actionContext){
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		Object dataObj = request.getParameter("dataObject").getValue();
		if(dataObj instanceof DataObject){
			DataObject dataObject = (DataObject) dataObj;
			return dataObject.delete(actionContext);
		}else{
			throw new ActionException("deleteDataObject： not a DataObject, it is a " + dataObj, actionContext);
		}
	}
	
	/**
	 * 查询列出数据。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Object listDataObjects(ActionContext actionContext){
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		Thing dataObject = (Thing) request.getParameter("dataObjectDescriptor").getValue();
		Thing conditionDescriptor = (Thing) request.getParameter("conditionDescriptor").getValue(); 
		Object conditionValues = (Object) request.getParameter("conditionValues").getValue();
		Object pageInfoObj = request.getParameter("pageInfo").getValue();
		
		return dataObject.doAction("query", actionContext, UtilMap.toMap(new Object[]{"conditionData",conditionValues, "conditionConfig",conditionDescriptor, "pageInfo",pageInfoObj}));		
	}
	
	@SuppressWarnings("rawtypes")
	public static PageInfo listDataObjectsPageInfo(ActionContext actionContext){
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		Thing dataObject = (Thing) request.getParameter("dataObjectDescriptor").getValue();
		Thing conditionDescriptor = (Thing) request.getParameter("conditionDescriptor").getValue(); 
		Object conditionValues = (Object) request.getParameter("conditionValues").getValue();
		Object pageInfoObj = request.getParameter("pageInfo").getValue();
		PageInfo pageInfo = PageInfo.getPageInfo(pageInfoObj);
		if(pageInfo == null){
			pageInfo = new PageInfo();
	        pageInfo.put("start", 0);
	        pageInfo.put("limit", 0);
	        if((Integer) pageInfo.get("limit") == 0){
	            pageInfo.put("limit", 0);
	        }
	        pageInfo.put("datas", new ArrayList<Object>());
	        pageInfo.put("success", true);
	        pageInfo.put("msg",  "");
	        pageInfo.put("totalCount", 0);
	        pageInfo.put("sort",  "");
	        pageInfo.put("dir", "");
		}
		
		Object datas = dataObject.doAction("query", actionContext, UtilMap.toMap(new Object[]{"conditionData",conditionValues, "conditionConfig",conditionDescriptor, "pageInfo",pageInfoObj}));
		if(datas instanceof List){
			List listDatas = (List) datas;
			pageInfo.put("datas", listDatas);
			pageInfo.setTotalCount(listDatas.size());
			return pageInfo;
		}else{
			return pageInfo;
		}
	}
	
	/**
	 * 获取数据对象的属性定义列表。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Object getDataObjectAttributeDescriptors(ActionContext actionContext){
		Thing dataObjectDescriptor = ThingFunctions.getThing("dataObjectDescriptor", actionContext);
		return dataObjectDescriptor.getChilds("attribute");
	}
	
	public static Object getDataObjectDescriptor(ActionContext actionContext){
		DataObject dataObject = (DataObject) actionContext.get("dataObject");
		return dataObject.getMetadata().getDescriptor();
	}
	
	public static Object getDataObjectKeys(ActionContext actionContext){
		DataObject dataObject = (DataObject) actionContext.get("dataObject");
		return dataObject.getMetadata().getKeyAndDatas();
	}
	
	public static Object getDataObjectKeyDescriptors(ActionContext actionContext){
		DataObject dataObject = (DataObject) actionContext.get("dataObject");
		return dataObject.getMetadata().getKeys();
	}
	
	public static DataObject getRecordById(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Object dataObject = actionContext.get("dataObject");
		DataObject data = null;
		if(dataObject instanceof Thing){
			data = new DataObject((Thing) dataObject);
		}else if(dataObject instanceof String){
			data = new DataObject((String) dataObject);
		}else{
			throw new ActionException("unkonown dataObject type, dataobject can be Thing or String, current is " + dataObject + ", path=" + self.getMetadata().getPath());
		}
		
		Thing[] keys = data.getMetadata().getKeys();
		if(keys == null || keys.length == 0 || keys.length > 1){
			throw new ActionException("DataObject key not exits or key more than one, path=" + self.getMetadata().getPath());
		}
		
		data.put(keys[0].getString("name"), actionContext.get("id"));
		return data.load(actionContext);
	}
	
	public static List<DataObject> getRecordsByIds(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Object dataObject = actionContext.get("dataObject");
		DataObject data = null;
		if(dataObject instanceof Thing){
			data = new DataObject((Thing) dataObject);
		}else if(dataObject instanceof String){
			data = new DataObject((String) dataObject);
		}else{
			throw new ActionException("unkonown dataObject type, dataobject can be Thing or String, current is " + dataObject + ", path=" + self.getMetadata().getPath());
		}
		
		Thing[] keys = data.getMetadata().getKeys();
		if(keys == null || keys.length == 0 || keys.length > 1){
			throw new ActionException("DataObject key not exits or key more than one, path=" + self.getMetadata().getPath());
		}
		
		String key = keys[0].getString("name");
		Thing conditionCfg = DataObjectUtil.createConditionThing(key, key);
		//兼容dbDataObject做的特殊设置
		String sql = keys[0].getStringBlankAsNull("sql");
		if(sql != null){
			conditionCfg.put("sqlColumn", sql);
		}else{
			conditionCfg.put("sqlColumn", keys[0].get("fieldName"));
		}
		
		return DataObjectUtil.query(data.getMetadata().getDescriptor(), 
				conditionCfg, 
				UtilMap.toMap(key, actionContext.get("id")), actionContext);
	}
	
}
