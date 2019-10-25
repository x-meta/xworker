package xworker.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 子系统一般都是独立线程执行的，通过SystemManager可以管理这些系统。
 * 
 * @author zyx
 *
 */
public class SystemManager {
	static Map<String, List<ISystem>> systemCache = new HashMap<String, List<ISystem>>();
	
	public static void regist(String path, ISystem system){
		List<ISystem> list = systemCache.get(path);		
		if(list == null){
			list = new ArrayList<ISystem>();
			systemCache.put(path, list);
		}
		
		list.add(system);
	}
	
	public static void unregist(String path, ISystem system){
		List<ISystem> list = systemCache.get(path);		
		if(list != null){
			list.remove(system);
		}
	}
	
	public static List<String> getPaths(){
		List<String> list = new ArrayList<String>();
		list.addAll(systemCache.keySet());
		
		return list;
	}
	
	public static List<ISystem> getSystems(String path){
		return systemCache.get(path);
	}
	
	
}
