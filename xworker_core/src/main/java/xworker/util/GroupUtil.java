package xworker.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 根据group列表分解成树的工具，比如：
 * test1.test2.test3
 * test1.test2.test4
 * test1
 * 分解为：
 * test1:
 *      test2:
 *            test3
 *            test4
 * 
 * @author Administrator
 *
 */
public class GroupUtil {
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getGroupMap(List<Map<String, Object>> groupDatas, 
			String groupName, String childsName, boolean sort, final String sortField){
		//对分组进行排序
		List<String> groups = new ArrayList<String>();
		Map<String, List<Map<String, Object>>> dataCaches = new HashMap<String, List<Map<String, Object>>>();
		
		for(Map<String, Object> data : groupDatas){
			String groupString = getGroupString(data, groupName);
			if(groupString == null){
				groupString = "";
			}
			for(String str : groupString.split("[,]")){				
				List<Map<String, Object>> cache = dataCaches.get(str);
				if(cache == null){
					cache = new ArrayList<Map<String, Object>>();
					dataCaches.put(str, cache);
					if(!"".equals(str)){
						groups.add(str);
					}
				}
				cache.add(data);
			}
		}		
				
		Collections.sort(groups);
		if(sort){			
			
			if(sortField != null && !"".equals(sortField)){
				for(String key : dataCaches.keySet()){
					List<Map<String, Object>> datas = dataCaches.get(key);
					Collections.sort(datas, new Comparator<Map<String, Object>>(){

						@Override
						public int compare(Map<String, Object> o1,
								Map<String, Object> o2) {
							Object k1 = o1.get(sortField);
							Object k2 = o2.get(sortField);
							if(k1 == null && k2 != null){
								return -1;
							}else if(k1 != null && k2 == null){
								return 1;
							}else{
								return String.valueOf(k1).compareTo(String.valueOf(k2));
							}
						}
						
					});
				}
			}
		}
		
		//设置分组
		Map<String, Object> context = new HashMap<String, Object>();
		initGroupData(context, "", childsName);
		
		for(String groupStr : groups){
			String path = null;
			String parentPath = "";
			for(String group : groupStr.split("[.]")){
				if(path == null){
					path = group;
				}else{
					path = parentPath + "." + group;
				}
				Map<String, Object> data = initGroupData(context, path, childsName);
				if(data != null){
					data.put("label", group);
					data.put(groupName, path);
					addToParent(context, data, parentPath, childsName);
				}
				
				if(!"".equals(parentPath)){
					parentPath = parentPath + ".";
				}
				parentPath = parentPath + group;
			}
		}
		
		for(String key : dataCaches.keySet()){
			List<Map<String,Object>> data = dataCaches.get(key);
			addChilds(context, key, data, childsName);
		}
		return (Map<String, Object>) context.get("");
	}
	
	@SuppressWarnings("unchecked")
	private static void addChilds(Map<String, Object> context, String group,List<Map<String, Object>> dataChilds, String childName){
		Map<String, Object> datas = (Map<String, Object>) context.get(group);
		List<Map<String, Object>> childs = (List<Map<String, Object>>) datas.get(childName);
		childs.addAll(dataChilds);
		
	}
	
	@SuppressWarnings("unchecked")
	private static void addToParent(Map<String, Object> context, Map<String, Object> group, String parentGroup, String childName){
		Map<String, Object> datas = (Map<String, Object>) context.get(parentGroup);
		List<Map<String, Object>> childs = (List<Map<String, Object>>) datas.get(childName);
		childs.add(group);
	}
	
	@SuppressWarnings("unchecked")
	private static Map<String, Object> initGroupData(Map<String, Object> context, String group, String childName){
		Map<String, Object> datas = (Map<String, Object>) context.get(group);
		if(datas == null){
			datas = new HashMap<String, Object>();
			datas.put(childName, new ArrayList<Map<String, Object>>());
			context.put(group, datas);
			return datas;
		}else{
			return null;
		}

	}
	
	private static String getGroupString(Map<String, Object> data, String groupName){
		Object groupObj = data.get(groupName);
		if(groupObj != null){
			return String.valueOf(groupObj);
		}else{
			return null;
		}		
	}
	
	/**
	 * 返回分组字符串的列表，比如xworker.swt,2|xworker.chart，返回[xworker.swt, xworker.chart]列表。
	 * @param group
	 * @return
	 */
	public static List<String> getGroups(String group){
		List<String> gs = new ArrayList<String>();
		if(group != null && !"".equals(group)) {
			for(String g : group.split("[,]")) {
				int index = g.indexOf("|");
				if(index != -1) {
					g = g.substring(index + 1, g.length());
				}
				g = g.trim();
				if(!"".equals(g)) {
					gs.add(g);
				}
			}
		}
		
		return gs;
	}
}
