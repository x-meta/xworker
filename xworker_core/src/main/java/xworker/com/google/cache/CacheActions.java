package xworker.com.google.cache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class CacheActions {
	private static Logger logger = LoggerFactory.getLogger(CacheActions.class);
	
	public static Object create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
		int concurrencyLevel = (Integer) self.doAction("getConcurrencyLevel", actionContext);
		if(concurrencyLevel > 0){
			builder.concurrencyLevel(concurrencyLevel);
		}
		
		long expireAfterAccess = (Long) self.doAction("getExpireAfterAccess", actionContext);
		if(expireAfterAccess >= 0){
			builder.expireAfterAccess(expireAfterAccess, TimeUnit.MILLISECONDS);
		}
		
		long expireAfterWrite = (Long) self.doAction("getExpireAfterWrite", actionContext);
		if(expireAfterWrite >= 0){
			builder.expireAfterWrite(expireAfterWrite, TimeUnit.MILLISECONDS);
		}
		
		int initialCapacity = (Integer) self.doAction("getInitialCapacity", actionContext);
		if(initialCapacity > 0){
			builder.initialCapacity(initialCapacity);
		}
		
		int maximumSize = (Integer) self.doAction("getMaximumSize", actionContext);
		if(maximumSize > 0){
			builder.maximumSize(maximumSize);
		}
		
		if(UtilData.isTrue(self.doAction("isSoftValues", actionContext))){
			builder.softValues();
		}
		
		if(UtilData.isTrue(self.doAction("isWeakKeys", actionContext))){
			builder.weakKeys();
		}
		
		if(UtilData.isTrue(self.doAction("isWeakValues", actionContext))){
			builder.weakValues();
		}
				
		return builder.build(new ThingLoader(self));		
	}
	
	@SuppressWarnings("unchecked")
	public static Object getCache(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		World world = World.getInstance();
		LoadingCache<Object, Object> cache = (LoadingCache<Object, Object>) world.getData(self.getMetadata().getPath() + "_cache");
		if(cache == null){
			cache = (LoadingCache<Object, Object>) self.doAction("create", actionContext);
			world.setData(self.getMetadata().getPath() + "_cache", cache);
		}
		return cache;
	}
	
	@SuppressWarnings("unchecked")
	public static Object run(ActionContext actionContext) throws ExecutionException{
		Thing self = actionContext.getObject("self");
		
		//获取缓存配置
		Thing cacheThing = (Thing) self.doAction("getCacheThing", actionContext);
		
		//获取缓存对象
		LoadingCache<Object, Object> cache = (LoadingCache<Object, Object>) cacheThing.doAction("getCache", actionContext);	
		Object key = self.doAction("getKey", actionContext);	
		
		//返回值
		return cache.get(key);
	}
	
	public static Object getGuavaCache(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		//获取缓存配置
		Thing cacheThing = (Thing) self.doAction("getCacheThing", actionContext);
		
		//获取缓存对象
		return cacheThing.doAction("getCache", actionContext);			
	}
	
	@SuppressWarnings("unchecked")
	public static void cleanCacheUp(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		//获取缓存配置
		Thing cacheThing = (Thing) self.doAction("getCacheThing", actionContext);
		
		//获取缓存对象
		LoadingCache<Object, Object> cache = (LoadingCache<Object, Object>) cacheThing.doAction("getCache", actionContext);	
		cache.cleanUp();
	}
	
	@SuppressWarnings("unchecked")
	public static Object getStats(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		//获取缓存配置
		Thing cacheThing = (Thing) self.doAction("getCacheThing", actionContext);
		
		//获取缓存对象
		LoadingCache<Object, Object> cache = (LoadingCache<Object, Object>) cacheThing.doAction("getCache", actionContext);	
		return cache.stats();
	}
	
	@SuppressWarnings("unchecked")
	public static void logPrintStats(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		//获取缓存配置
		Thing cacheThing = (Thing) self.doAction("getCacheThing", actionContext);
		
		//获取缓存对象
		LoadingCache<Object, Object> cache = (LoadingCache<Object, Object>) cacheThing.doAction("getCache", actionContext);	
		logger.info(cache.stats().toString());
	}
	
	public static void remove(ActionContext actionContext){
		Thing self = actionContext.getObject("self");		
		String refCache = self.getStringBlankAsNull("refCache");
		if(refCache == null){
			World.getInstance().setData(self.getMetadata().getPath() + "_cache", null);
		}		
	}
	
	@SuppressWarnings("unchecked")
	public static void cleanUp(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		//是否是引用，如果是使用引用
		String refCache = self.getStringBlankAsNull("refCache");
		if(refCache != null){
			return;
		}
		
		LoadingCache<Object, Object> cache = (LoadingCache<Object, Object>) self.doAction("getCache", actionContext);
		cache.cleanUp();
	}
	
	@SuppressWarnings("unchecked")
	public static void printStatus(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		//是否是引用，如果是使用引用
		String refCache = self.getStringBlankAsNull("refCache");
		if(refCache != null){
			Thing cacheThing = World.getInstance().getThing(refCache);
			cacheThing.doAction("printStatus", actionContext);
		}
		
		LoadingCache<Object, Object> cache = (LoadingCache<Object, Object>) self.doAction("getCache", actionContext);
		System.out.println(cache.stats().toString());
	}
	
	static class ThingLoader extends CacheLoader<Object, Object>{
		Thing thing;
		ActionContext actionContext = new ActionContext();
		
		public ThingLoader(Thing thing){
			this.thing = thing;
		}
		
		@Override
		public Object load(Object key) throws Exception {
			return thing.doAction("load", actionContext, "key", key);
		}
	}
}
