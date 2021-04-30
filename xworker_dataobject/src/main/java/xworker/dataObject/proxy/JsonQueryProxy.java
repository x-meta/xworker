package xworker.dataObject.proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ExceptionUtil;

import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectConstants;
import xworker.dataObject.DataObjectList;
import xworker.dataObject.PageInfo;
import xworker.dataObject.cache.DataObjectCache;
import xworker.util.JacksonFormator;

/**
 * <p>JSON数据对象代理，通常是为了远程调用数据对象。除了查询也支持其他方法，如create、update和delete。</p>
 * 
 * @author zhangyuxiang
 *
 */
public class JsonQueryProxy {
	public static String formatBatchAction(String action, String dataObjectPath, String conditionPath, Object conditionData) throws Exception {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("action", action);
		data.put("dataObjectPath", dataObjectPath);
		Thing condition = World.getInstance().getThing(conditionPath);
		if(condition != null) {
			if(condition.isTransient()) {
				data.put("condition", condition.toXML());
			}else {
				data.put("conditionPath", conditionPath);
			}
		}
		data.put("conditionData", conditionData);
		return JacksonFormator.formatObject(data);
	}

	/**
	 * 执行批量的操作。支持deleteBatch和updateBatch两个方法。
	 *
	 * @param json
	 * @param actionContext
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static String doBatchAction(String json, ActionContext actionContext) throws Exception {
		Map<String, Object> params = (Map<String, Object>) JacksonFormator.parseObject(json);
		try {
			String action = (String) params.get("action");
			Thing dataObjectThing = World.getInstance().getThing((String) params.get("dataObjectPath"));
			Thing condition = World.getInstance().getThing((String) params.get("conditionPath"));
			if(condition == null) {
				String conditionXML = (String) params.get("condition");
				if(conditionXML != null) {
					condition = new Thing();
					condition.parseXML(conditionXML);
				}
			}
			Map<String, Object> conditionData = (Map<String, Object>) params.get("conditionData");
			if(dataObjectThing != null) {
				DataObject dataObject = new DataObject(dataObjectThing);
				action = action.toLowerCase();
				if("deleteBatch".equals(action)) {
					Object result = dataObject.delete(actionContext, condition, conditionData);
					params.put("result", result);
				}else if("updateBatch".equals(action)) {
					Object result = dataObject.update(actionContext, condition, conditionData);
					params.put("result", result);
				}
			}
		}catch(Exception e) {
			params.put("exception", ExceptionUtil.getCause(e).getMessage());
		}
		
		return JacksonFormator.formatObject(params);
	}
	
	/**
	 * 格式化数据对象的增、删改等动作到JSON，用于请求相关操作。
	 * 
	 * @param dataObjectPath  目标数据对象
	 * @param dataObject 当前数据对象，如DataObject、Map或对象等，和目标数据对象的字段对应
	 * @param action
	 * @return
	 * @throws Exception 
	 */
	public static String formatDataObjectAction(String dataObjectPath, Object dataObject, String action) throws Exception {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("action", action);
		data.put("dataObjectPath", dataObjectPath);
		data.put("data", dataObject);
		return JacksonFormator.formatObject(data);
	}
	
	@SuppressWarnings("unchecked")
	public static String doDataObjectAction(String paramsJson, ActionContext actionContext) throws Exception {
		Map<String, Object> params = (Map<String, Object>) JacksonFormator.parseObject(paramsJson);
		try {
			String action = (String) params.get("action");
			Map<String, Object> data = (Map<String, Object>) params.get("data");
			Thing dataObjectThing = World.getInstance().getThing((String) params.get("dataObjectPath"));
			if(dataObjectThing != null) {
				DataObject dataObject = new DataObject(dataObjectThing);
				dataObject.putAll(data);
				action = action.toLowerCase();
				if("create".equals(action)) {
					dataObject = dataObject.create(actionContext);
					params.put("data", dataObject);
					params.put("result", dataObject != null);
				}else if("load".equals(action)) {
					dataObject = dataObject.load(actionContext);
					params.put("data", dataObject);
					params.put("result", dataObject != null);
				}else if("update".equals(action)) {
					int count = dataObject.update(actionContext);
					params.put("result", count);
				}else if("delete".equals(action)) {
					int count = dataObject.delete(actionContext);
					params.put("result", count);							
				}
			}
		}catch(Exception e) {
			params.put("exception", ExceptionUtil.getRootCause(e).getMessage());
		}
		
		return JacksonFormator.formatObject(params);
	}
	
	@SuppressWarnings("unchecked")
	public static Object parseBatchResult(String resultJson) throws Exception {
		Map<String, Object> data = (Map<String, Object>) JacksonFormator.parseObject(resultJson);
		String exception = (String) data.get("exception");
		if(exception != null) {
			throw new Exception(exception);
		}
		
		return data.get("result");
	}
	
	/**
	 * <p>分析执行数据对象动作的结果。结果是Map，Map中的值有action，dataObjectPath，data和result。</p>
	 * 
	 * action是create、load、update或delete。
	 * data如果是load、create那么是加载和创建的数据对象，否则是null。对于update和delete是传入的值。
	 * result是结果，load和create是boolean，update和delete是数量(int)。
	 * 
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Object parseDataObjectActionResult(String result) throws Exception {
		Map<String, Object> data = (Map<String, Object>) JacksonFormator.parseObject(result);
		String exception = (String) data.get("exception");
		if(exception != null) {
			throw new Exception(exception);
		}
		String action = (String) data.get("action");
		if("create".equals(action)) {
			return data.get("data");
		}else if("load".equals(action)) {
			return data.get("data");
		}else if("update".equals(action)) {
			return data.get("result");
		}else if("delete".equals(action)) {
			return data.get("result");							
		}
		return null;
	}
	
	/**
	 * 把查询条件包装成JSON。
	 * 
	 * @param dataObjectPath
	 * @param queryConfigPath
	 * @param params
	 * @param pageInfo
	 * @return
	 * @throws Exception
	 */
	public static String formatParams(String dataObjectPath, String queryConfigPath, Map<String, Object> params, PageInfo pageInfo) throws Exception {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("dataObjectPath", dataObjectPath);
		Thing queryConfig = World.getInstance().getThing(queryConfigPath);
		if(queryConfig != null) {
			if(queryConfig.isTransient()) {
				data.put("queryConfig", queryConfig.toXML());
			} else {
				data.put("queryConfigPath", queryConfigPath);
			}
		}
		data.put("params", params);
		
		Map<String, Object> page = new HashMap<String, Object>();
		if(pageInfo != null){
			page.putAll(pageInfo.getPageInfoData());
		}else {
			PageInfo pp = new PageInfo(page);
			pp.setPage(0);
			pp.setPageSize(100);
			pp.setLimit(100);
			pp.setStart(0);
		}
		
		data.put("pageInfo", page);
		String json =JacksonFormator.formatObject(data);
		return json;
	}
	
	public static String query(String dataObjectPath, String queryConfigPath, Map<String, Object> params, Map<String, Object> pageInfo, ActionContext actionContext) throws Exception {
		World world = World.getInstance();
		return query(world.getThing(dataObjectPath), world.getThing(queryConfigPath), params, pageInfo, actionContext);
	}
	
	public static String query(Thing dataObject, Thing queryConfig, Map<String, Object> params, Map<String, Object> pageInfo, ActionContext actionContext) throws Exception {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("dataObjectPath", dataObject.getMetadata().getPath());
		if(queryConfig != null) {
			if(queryConfig.isTransient()) {
				data.put("queryConfig", queryConfig.toXML());
			} else {
				data.put("queryConfigPath", queryConfig.getMetadata().getPath());
			}
		}
		data.put("params", params);
		data.put("pageInfo", pageInfo);
		
		return query(dataObject, queryConfig, params, pageInfo, data, actionContext);
	}
	
	private static String query(Thing dataObject, Thing queryConfig, Map<String, Object> params, Map<String, Object> pageInfo, Map<String, Object> data, ActionContext actionContext) throws Exception {
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		if(dataObject != null) {
			List<DataObject> ds = dataObject.doAction("query", actionContext, 
					"conditionData", params, "conditionConfig", queryConfig, "pageInfo", pageInfo);
			for(DataObject d : ds) {
				Map<String, Object> theData = new HashMap<String, Object>();
				for(Thing attribute : dataObject.getChilds("attribute")) {
					String name = attribute.getMetadata().getName();
					if(name.contains(".")){
						//关联字段属性不设置
						continue;
					}
					Object object = d.get(name);
					if(object instanceof DataObject || object instanceof DataObjectList){
						//关联的数据对象和数据对象列表也不格式化
						continue;
					}
					theData.put(name, d.get(name));
				}
				datas.add(theData);
			}
		}
		data.put("datas", datas);
		return JacksonFormator.formatObject(data);
	}
	/**
	 * 查询并把结果包装成JSON。
	 * 
	 * @param paramsJson 格式{"dataObjectPath":"", "queryConfigPath":"", "params":{}, "pageInfo":{}}
	 * @param actionContext
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public static String query(String paramsJson, ActionContext actionContext) throws Exception {
		Map<String, Object> data = (Map<String, Object>) JacksonFormator.parseObject(paramsJson);
		World world = World.getInstance();
		Thing dataObject = world.getThing((String) data.get("dataObjectPath"));		
		Thing queryConfig = world.getThing((String) data.get("queryConfigPath"));
		if(queryConfig == null) {
			String queryConfigXml = (String) data.get("queryConfig");
			if(queryConfigXml != null) {
				queryConfig = new Thing();
				queryConfig.parseXML(queryConfigXml);
			}
		}
		Map<String, Object> params = (Map<String, Object>) data.get("params");
		Map<String, Object> pageInfo = (Map<String, Object>) data.get("pageInfo");
				
		return query(dataObject, queryConfig, params, pageInfo, data, actionContext);
	}
	
	/**
	 * 解析查询的结果。
	 * 
	 * @param resultJson
	 * @param actionContext
	 * @return
	 * @throws Exception
	 */	
	public static List<DataObject> parseResults(String resultJson, ActionContext actionContext) throws Exception{
		return parseQueryResults(resultJson, actionContext).getDatas();
	}
	
	@SuppressWarnings("unchecked")
	public static PageInfo parseQueryResults(String resultJson, ActionContext actionContext) throws Exception{
		Map<String, Object> data = (Map<String, Object>) JacksonFormator.parseObject(resultJson);
		World world = World.getInstance();
		Thing dataObject = world.getThing((String) data.get("dataObjectPath"));
		if(dataObject == null) {
			throw new ActionException("DataObject is null");
		}
		
		Map<String, Object> pInfo = (Map<String, Object>) data.get("pageInfo");
		Map<String, Object> pageInfo = actionContext.getObject("pageInfo");
		List<Map<String, Object>> datas = (List<Map<String, Object>>) data.get("datas");
		List<DataObject> ds = new ArrayList<DataObject>();
		DataObjectCache.begin();
		try {
			for (Map<String, Object> d : datas) {
				DataObject theData = new DataObject(dataObject);
				theData.putAll(d);

				theData.fireOnLoaded(actionContext);
				ds.add(theData);
			}
		}finally {
			DataObjectCache.finish();
		}
		
		if(pageInfo == null) {
			PageInfo pg = new PageInfo();
			pg.setDatas(ds);
			return pg;
		}else {
			if(pInfo != null) {
				pageInfo.putAll(pInfo);
			}
			pageInfo.put(DataObjectConstants.PAGEINFO_DATAS, ds);
			return new PageInfo(pageInfo);
		}
	}	
}
