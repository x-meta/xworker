package xworker.statistics.flow.mapstore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmeta.ActionContext;

import xworker.statistics.flow.MapStore;

public class HashMapStore {
	HashMap<String, Object> cache = new HashMap<String, Object>();
	
	public synchronized void inc(String key , long count){
		Long c = (Long) cache.get(key);
		if(c == null){
			cache.put(key, count);
		}else{
			c = c + count;
			cache.put(key,  c);
		}
	}
	
	public long getKeyCount(String key){
		Long c = (Long) cache.get(key);
		if(c == null){
			return 0;
		}else{
			return c;
		}		
	}
	
	@SuppressWarnings("unchecked")
	public synchronized void addToKeyList(String key, String keyManager){
		List<String> keys = (List<String>) cache.get(keyManager);
		if(keys == null){
			keys = new ArrayList<String>();
			keys.add(key);
			cache.put(keyManager, keys);
		}else{// if(!keys.contains(key)){
			keys.add(key);
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean exists(String key, String keyManager){
		return cache.get(key) != null;
		/*
		List<String> keys = (List<String>) cache.get(keyManager);
		if(keys == null){
			return false;
		}else{
			return keys.contains(key);
		}*/
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getKeyList(String keyManager){
		return (List<String>) cache.get(keyManager);
	}
	
	@SuppressWarnings("unchecked")
	public void removeKey(String key, String keyManager){
		cache.remove(key);
		List<String> keys = (List<String>) cache.get(keyManager);
		if(keys != null){
			keys.remove(key);
			if(keys.size() == 0){
				cache.remove(keyManager);
			}
		}
	}
	
	public synchronized static HashMapStore getMapStore(ActionContext actionContext){
		MapStore mapStore = actionContext.getObject("mapStore");
		if(mapStore.object == null){
			mapStore.object = new HashMapStore();
		}
		
		return (HashMapStore) mapStore.object;
	}
	
	public static void inc(ActionContext actionContext){
		HashMapStore mapStore = getMapStore(actionContext);
		mapStore.inc((String) actionContext.get("key"), (Long) actionContext.get("count")); 
	}
	
	public static long getKeyCount(ActionContext actionContext){
		HashMapStore mapStore = getMapStore(actionContext);
		return mapStore.getKeyCount((String) actionContext.get("key")); 
	}
	
	public static void addToKeyList(ActionContext actionContext){
		HashMapStore mapStore = getMapStore(actionContext);
		mapStore.addToKeyList((String) actionContext.get("key"), (String) actionContext.get("keyManager")); 
	}
	
	public static boolean exists(ActionContext actionContext){
		HashMapStore mapStore = getMapStore(actionContext);
		return mapStore.exists((String) actionContext.get("key"), (String) actionContext.get("keyManager")); 
	}
	
	public static List<String> getKeyList(ActionContext actionContext){
		HashMapStore mapStore = getMapStore(actionContext);
		return mapStore.getKeyList((String) actionContext.getObject("keyManager")); 
	}
	
	public static void removeKey(ActionContext actionContext){
		HashMapStore mapStore = getMapStore(actionContext);
		mapStore.removeKey((String) actionContext.get("key"), (String) actionContext.get("keyManager")); 
	}
}
