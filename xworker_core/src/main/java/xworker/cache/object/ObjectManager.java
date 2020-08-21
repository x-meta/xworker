package xworker.cache.object;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

/**
 * 各种对象实例的管理。
 * 
 * @author Administrator
 *
 */
public class ObjectManager {
	/**
	 * 对象缓存。
	 */
	private static Map<String, Map<String, ThingObject<?>>> objectCaches = new HashMap<String, Map<String, ThingObject<?>>>();
	
	/**
	 * 使用事物的描述者作为类型获对象上下文。
	 * 
	 * @param thing
	 * @return
	 */
	public static ThingObject<?> get(Thing thing){
		return get(thing.getDescriptor().getMetadata().getPath(), thing);
	}
	
	/**
	 * 获取对象上下文。
	 * 
	 * @param type
	 * @param thing
	 * @return
	 */
	public static ThingObject<?> get(String type, Thing thing){
		Map<String, ThingObject<?>> contexts = objectCaches.get(type);
		if(contexts != null){
			return contexts.get(thing.getMetadata().getPath());
		}
		
		return null;
	}
	
	/**
	 * 使用事物的描述者作为类型放入仪的对象上下文。
	 * 
	 * @param thing
	 * @param object
	 * @param actionContext
	 */
	public static void put(Thing thing, Object object, ActionContext actionContext){
		put(thing.getDescriptor().getMetadata().getPath(), thing, object, actionContext);
	}
	
	/**
	 * 放入一个对象上下文。
	 * 
	 * @param type
	 * @param thing
	 * @param object
	 * @param actionContext
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void put(String type, Thing thing, Object object, ActionContext actionContext){
		Map<String, ThingObject<?>> contexts = objectCaches.get(type);
		if(contexts == null){
			contexts = new HashMap<String, ThingObject<?>>();
			objectCaches.put(type, contexts);
		}
		
		ThingObject<?> context = new ThingObject(thing, object, actionContext);
		contexts.put(thing.getMetadata().getPath(), context);
	}
	
	/**
	 * 使用事物的描述者作为类型移除一个对象上下文。
	 * 
	 * @param thing
	 */
	public static void remove(Thing thing){
		remove(thing.getDescriptor().getMetadata().getPath(), thing);
	}
	
	/**
	 * 移除一个对象上下文。
	 * 
	 * @param type
	 * @param thing
	 */
	public static void remove(String type, Thing thing){
		Map<String, ThingObject<?>> contexts = objectCaches.get(type);
		if(contexts != null){
			contexts.remove(thing.getMetadata().getPath());
		}
	}
	
	/**
	 * 返回所有的类型。
	 * 
	 * @return
	 */
	public static List<String> getAllTypes(){
		List<String> list = new ArrayList<String>();
		for(String key : objectCaches.keySet()){
			list.add(key);
		}
		
		Collections.sort(list);
		
		return list;
	}
	
	public static List<ThingObject<?>> getThingObjectList(String type){
		Map<String, ThingObject<?>> contexts = objectCaches.get(type);
		List<ThingObject<?>> list = new ArrayList<ThingObject<?>>();
		if(contexts != null){
			for(String key : contexts.keySet()){ 
				list.add(contexts.get(key));
			}
		}
		
		return list;
	}
	
	public static Map<String, ThingObject<?>> getThingObjectMap(String type){
		return objectCaches.get(type);
	}
}
