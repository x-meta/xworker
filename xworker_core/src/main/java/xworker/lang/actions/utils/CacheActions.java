package xworker.lang.actions.utils;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.util.UtilData;

public class CacheActions {
	private static Logger logger = LoggerFactory.getLogger(CacheActions.class);
	
	public static Object getCachedObjectOnThing(final ActionContext actionContext) {
		final Thing self = actionContext.getObject("self");
		
		//key
		final String key = "__getCachedObjectOnThing__cacheObject__";
		final String key_time = "__getCachedObjectOnThing__cachetime__";
		
		//缓存对象
		Object obj = self.get(key);
		
		//是否要重新加载
		long reloadTime = self.doAction("getReloadTime", actionContext);
		boolean reload = obj == null;
		if(!reload && reloadTime > 0) {
			long lastTime = self.getLong(key_time, 0);
			if(System.currentTimeMillis() - lastTime > reloadTime * 1000) {
				reload = true;
			}
		}
		
		//重新加载缓存对象
		if(reload) {
			if(UtilData.isTrue(self.doAction("isLoadBackground", actionContext))) {
				final Map<String, Object> params = self.doAction("getParams", actionContext);
				new Thread(new Runnable() {
					public void run() {
						try {
							Object obj = self.doAction("doLoad", actionContext, params);
							self.put(key, obj);
							self.put(key_time, System.currentTimeMillis());							
						}catch(Exception e) {
							logger.warn("Load cache exception, action=" + self.getMetadata().getPath(), e);
						}
					}
				}).start();
			}else {
				obj = self.doAction("doLoad", actionContext);
				self.put(key_time, System.currentTimeMillis());
				self.put(key, obj);
			}
		}
		
		return obj;
	}
}
