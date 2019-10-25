package xworker.lang.actions.utils;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class MapUtilsActions {
	public static Map<String, Object> createMapByChilds(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Map<String, Object> map = new HashMap<String, Object>();
		for(Thing child : self.getChilds()){
			String key = child.getMetadata().getName();
			Object value = child.getAction().run(actionContext, null, true);
			map.put(key, value);
		}
		
		return map;
	}
	
	@SuppressWarnings("rawtypes")
	public static Object getObject(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Map map = (Map) self.doAction("getMap", actionContext);
		Object key = self.doAction("getKey", actionContext);
		
		Object result = map.get(key);
		if(result == null){
			return self.doAction("getDefaultValue", actionContext);
		}else{
			return result;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static Object getString(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Map map = (Map) self.doAction("getMap", actionContext);
		Object key = self.doAction("getKey", actionContext);
				
		Object result = MapUtils.getString(map, key);
		if(result == null){
			return self.doAction("getDefaultValue", actionContext);
		}else{
			return result;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static Object getBoolean(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Map map = (Map) self.doAction("getMap", actionContext);
		Object key = self.doAction("getKey", actionContext);
				
		Object result = MapUtils.getBoolean(map, key);
		if(result == null){
			return self.doAction("getDefaultValue", actionContext);
		}else{
			return result;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static Object getNumber(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Map map = (Map) self.doAction("getMap", actionContext);
		Object key = self.doAction("getKey", actionContext);
				
		Object result = MapUtils.getNumber(map, key);
		if(result == null){
			return self.doAction("getDefaultValue", actionContext);
		}else{
			return result;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static Object getByte(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Map map = (Map) self.doAction("getMap", actionContext);
		Object key = self.doAction("getKey", actionContext);
				
		Object result = MapUtils.getByte(map, key);
		if(result == null){
			return self.doAction("getDefaultValue", actionContext);
		}else{
			return result;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static Object getShort(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Map map = (Map) self.doAction("getMap", actionContext);
		Object key = self.doAction("getKey", actionContext);
				
		Object result = MapUtils.getShort(map, key);
		if(result == null){
			return self.doAction("getDefaultValue", actionContext);
		}else{
			return result;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static Object getInteger(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Map map = (Map) self.doAction("getMap", actionContext);
		Object key = self.doAction("getKey", actionContext);
				
		Object result = MapUtils.getInteger(map, key);
		if(result == null){
			return self.doAction("getDefaultValue", actionContext);
		}else{
			return result;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static Object getLong(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Map map = (Map) self.doAction("getMap", actionContext);
		Object key = self.doAction("getKey", actionContext);
				
		Object result = MapUtils.getLong(map, key);
		if(result == null){
			return self.doAction("getDefaultValue", actionContext);
		}else{
			return result;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static Object getFloat(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Map map = (Map) self.doAction("getMap", actionContext);
		Object key = self.doAction("getKey", actionContext);
				
		Object result = MapUtils.getFloat(map, key);
		if(result == null){
			return self.doAction("getDefaultValue", actionContext);
		}else{
			return result;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static Object getDouble(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Map map = (Map) self.doAction("getMap", actionContext);
		Object key = self.doAction("getKey", actionContext);
				
		Object result = MapUtils.getDouble(map, key);
		if(result == null){
			return self.doAction("getDefaultValue", actionContext);
		}else{
			return result;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static Object getMap(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Map map = (Map) self.doAction("getMap", actionContext);
		Object key = self.doAction("getKey", actionContext);
				
		Object result = MapUtils.getMap(map, key);
		if(result == null){
			return self.doAction("getDefaultValue", actionContext);
		}else{
			return result;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static Object toProperties(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Map map = (Map) self.doAction("getMap", actionContext);
				
		return MapUtils.toProperties(map);	
	}
	
	@SuppressWarnings("rawtypes")
	public static void verbosePrint(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Map map = (Map) self.doAction("getMap", actionContext);
		String label = (String) self.doAction("getLabel", actionContext);
		PrintStream out = (PrintStream) self.doAction("getOut", actionContext);
				
		MapUtils.verbosePrint(out, label, map);	
	}
	
	@SuppressWarnings("rawtypes")
	public static void debugPrint(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Map map = (Map) self.doAction("getMap", actionContext);
		String label = (String) self.doAction("getLabel", actionContext);
		PrintStream out = (PrintStream) self.doAction("getOut", actionContext);
				
		MapUtils.debugPrint(out, label, map);	
	}
	
	@SuppressWarnings("rawtypes")
	public static Object invertMap(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Map map = (Map) self.doAction("getMap", actionContext);
				
		return MapUtils.toProperties(map);	
	}
	
	@SuppressWarnings("rawtypes")
	public static void safeAddToMap(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Map map = (Map) self.doAction("getMap", actionContext);
		Object key = self.doAction("getKey", actionContext);
		Object value = self.doAction("getValue", actionContext);
				
		MapUtils.safeAddToMap(map, key, value);		
	}
	
	@SuppressWarnings("rawtypes")
	public static Object emptyIfNull(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Map map = (Map) self.doAction("getMap", actionContext);
		
		if(map == null){
			return new HashMap();
		}else{
			return map;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static Object isEmpty(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Map map = (Map) self.doAction("getMap", actionContext);
		
		return MapUtils.isEmpty(map);
	}
	
	@SuppressWarnings("rawtypes")
	public static Object isNotEmpty(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Map map = (Map) self.doAction("getMap", actionContext);
		
		return MapUtils.isEmpty(map);
	}
	
	@SuppressWarnings("rawtypes")
	public static Object unmodifiableMap(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Map map = (Map) self.doAction("getMap", actionContext);
		
		return MapUtils.unmodifiableMap(map);
	}
	
	@SuppressWarnings("rawtypes")
	public static Object synchronizedMap(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Map map = (Map) self.doAction("getMap", actionContext);
		
		return MapUtils.synchronizedMap(map);
	}
	
	@SuppressWarnings("rawtypes")
	public static Object fixedSizeMap(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Map map = (Map) self.doAction("getMap", actionContext);
		
		return MapUtils.fixedSizeMap(map);
	}
	
	@SuppressWarnings("rawtypes")
	public static Object orderedMap(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Map map = (Map) self.doAction("getMap", actionContext);
		
		return MapUtils.orderedMap(map);
	}
	
	@SuppressWarnings("rawtypes")
	public static Object iterate(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Map map = (Map) self.doAction("getMap", actionContext);
		Object result = null;
		for(Object key : map.keySet()){
			Object value = map.get(key);
			result = self.doAction("handle", actionContext, "key", key, "map", map, "value", value);
			
            int status = actionContext.getStatus();	            
            if(status == ActionContext.BREAK){
                actionContext.setStatus(ActionContext.RUNNING);
                break;
            }else if(status == ActionContext.CONTINUE){
                actionContext.setStatus(ActionContext.RUNNING);
                continue;
            }else if(status == ActionContext.RETURN){
          	    break;
            }
            
		}
		return result;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void putValues(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Map map = (Map) self.doAction("getMap", actionContext);
		for(Thing child : self.getChilds()){
			String key = child.getMetadata().getName();
			Object value = child.getAction().run(actionContext, null, true);
			map.put(key, value);
		}
	}
}

