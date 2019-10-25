package xworker.statistics.flow.mapstore;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import xworker.statistics.flow.MapStore;

public class RedisMapStore {
	JedisPool jedisPool;
	
	public RedisMapStore(String host, int port, String password){
		if(password != null && !"".equals(password)){
			jedisPool = new JedisPool(new JedisPoolConfig(), host, port, 5000, password);
		}else{
			jedisPool = new JedisPool(new JedisPoolConfig(), host, port, 5000);
		}
	}
	
	public synchronized void inc(String key , long count){
		Jedis jedis = jedisPool.getResource();
		try{
			jedis.incrBy(key, count);
		}finally{
			jedis.close();
		}
	}
	
	public long getKeyCount(String key){
		Jedis jedis = jedisPool.getResource();
		try{
			String value = jedis.get(key);
			if(value == null){
				return 0;
			}else{
				return Long.parseLong(value);
			}
		}finally{
			jedis.close();
		}	
	}
	
	public synchronized void addToKeyList(String key, String keyManager){
		Jedis jedis = jedisPool.getResource();
		try{
			jedis.sadd(keyManager, key);
		}finally{
			jedis.close();
		}
	}
	
	public boolean exists(String key, String keyManager){
		Jedis jedis = jedisPool.getResource();
		try{
			return jedis.exists(key);
		}finally{
			jedis.close();
		}
	}
	
	public List<String> getKeyList(String keyManager){
		Jedis jedis = jedisPool.getResource();
		try{
			List<String> keys = new ArrayList<String>();
			Set<String> ksets = jedis.smembers(keyManager);
			keys.addAll(ksets);
			return keys;
		}finally{
			jedis.close();
		}
	}
	
	public void removeKey(String key, String keyManager){
		Jedis jedis = jedisPool.getResource();
		try{
			jedis.del(key);			
			jedis.srem(keyManager, key);
			if(jedis.scard(keyManager) == 0){
				jedis.del(keyManager);
			}
		}finally{
			jedis.close();
		}
	}
	
	public synchronized static RedisMapStore getMapStore(ActionContext actionContext){
		MapStore mapStore = actionContext.getObject("mapStore");
		if(mapStore.object == null){
			Thing self = actionContext.getObject("self");			
			String host = self.doAction("getHost", actionContext);
			int port = self.doAction("getPort", actionContext);
			String password = self.doAction("getPassword", actionContext);
			
			mapStore.object = new RedisMapStore(host, port, password);
		}
		
		return (RedisMapStore) mapStore.object;
	}
	
	public static void inc(ActionContext actionContext){
		RedisMapStore mapStore = getMapStore(actionContext);
		mapStore.inc((String) actionContext.get("key"), (Long) actionContext.get("count")); 
	}
	
	public static long getKeyCount(ActionContext actionContext){
		RedisMapStore mapStore = getMapStore(actionContext);
		return mapStore.getKeyCount((String) actionContext.get("key")); 
	}
	
	public static void addToKeyList(ActionContext actionContext){
		RedisMapStore mapStore = getMapStore(actionContext);
		mapStore.addToKeyList((String) actionContext.get("key"), (String) actionContext.get("keyManager")); 
	}
	
	public static boolean exists(ActionContext actionContext){
		RedisMapStore mapStore = getMapStore(actionContext);
		return mapStore.exists((String) actionContext.get("key"), (String) actionContext.get("keyManager")); 
	}
	
	public static List<String> getKeyList(ActionContext actionContext){
		RedisMapStore mapStore = getMapStore(actionContext);
		return mapStore.getKeyList((String) actionContext.getObject("keyManager")); 
	}
	
	public static void removeKey(ActionContext actionContext){
		RedisMapStore mapStore = getMapStore(actionContext);
		mapStore.removeKey((String) actionContext.get("key"), (String) actionContext.get("keyManager")); 
	}
	
	public static void close(ActionContext actionContext){
		RedisMapStore mapStore = getMapStore(actionContext);
		mapStore.jedisPool.close(); 
	}
}
