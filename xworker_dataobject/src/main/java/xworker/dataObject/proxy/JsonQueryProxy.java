package xworker.dataObject.proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.dataObject.DataObject;
import xworker.dataObject.DataObjectConstants;
import xworker.dataObject.PageInfo;
import xworker.dataObject.utils.JacksonFormator;

/**
 * 把查询和结果封装为JSON的代理。
 * 
 * @author zhangyuxiang
 *
 */
public class JsonQueryProxy {
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
		data.put("queryConfigPath", queryConfigPath);
		data.put("params", params);
		
		Map<String, Object> page = new HashMap<String, Object>();
		PageInfo pp = new PageInfo(page);
		if(pageInfo != null) {
			pp.setDir(pageInfo.getDir());
			pp.setPage(pageInfo.getPage());
			pp.setPageSize(pageInfo.getPageSize());
			pp.setLimit(pageInfo.getLimit());
		}else {
			pp.setPage(0);
			pp.setPageSize(200);
			pp.setLimit(200);
		}
		
		data.put("pageInfo", page);
		String json =JacksonFormator.formatObject(data);
		return json;
	}
	
	/**
	 * 查询并把结果包装成JSON。
	 * 
	 * @param paramsJson
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
		Map<String, Object> params = (Map<String, Object>) data.get("params");
		Map<String, Object> pageInfo = (Map<String, Object>) data.get("pageInfo");
				
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		if(dataObject != null) {
			List<DataObject> ds = dataObject.doAction("query", actionContext, 
					"conditionData", params, "conditionConfig", queryConfig, "pageInfo", pageInfo);
			for(DataObject d : ds) {
				Map<String, Object> theData = new HashMap<String, Object>();
				for(Thing attribute : dataObject.getChilds("attribute")) {
					String name = attribute.getMetadata().getName(); 
					theData.put(name, d.get(name));
				}
				datas.add(theData);
			}
		}
		data.put("datas", datas);
		return JacksonFormator.formatObject(data);
	}
	
	/**
	 * 解析结果。
	 * 
	 * @param resultJson
	 * @param actionContext
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static List<DataObject> parseResults(String resultJson, ActionContext actionContext) throws Exception{
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
		for(Map<String, Object> d : datas) {
			DataObject theData = new DataObject(dataObject);
			theData.putAll(d);
			ds.add(theData);
		}
		
		if(pageInfo != null) {
			if(pInfo != null) {
				pageInfo.putAll(pInfo);
			}
			pageInfo.put(DataObjectConstants.PAGEINFO_DATAS, ds);
		}
		
		return ds;
	}
}
