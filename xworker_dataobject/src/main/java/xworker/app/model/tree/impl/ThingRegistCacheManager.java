package xworker.app.model.tree.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.XMetaTimerManager;

import xworker.app.model.tree.impl.ThingRegistTreeModelActions.Group;

public class ThingRegistCacheManager {
	private static Logger logger = LoggerFactory.getLogger(ThingRegistCacheManager.class);
	
	static Map<String, ThingRegistCache> caches = new HashMap<String, ThingRegistCache>();
	
	private static boolean isReloading = false;
	
	static{
		XMetaTimerManager.schedule(new CheckExpireTask(), 0, 5000);
	}
	
	public static void putCache(Thing thingRegistModel, String thingPath, String type, String child, Map<String, Group> datas){
		if(thingRegistModel.getBoolean("cache")){
			long expireTime = thingRegistModel.getLong("expireTime");
			if(expireTime <= 0){
				expireTime = 300000;
			}
			long refreshInterval = thingRegistModel.getLong("refreshInterval");
			if(refreshInterval < 0){
				refreshInterval = 0;
			}
			
			String key = getKey(thingPath, type, child);
			ThingRegistCache cache = caches.get(key); 
			if(cache == null){
				cache = new ThingRegistCache(thingPath, type, child, expireTime, refreshInterval, datas);
				caches.put(key, cache);
			}else{
				cache.setExpireTime(expireTime);
				cache.setRefreshInterval(refreshInterval);
				cache.setDatas(datas);
			}
		}
	}
	
	public static ThingRegistCache getCache(String thingPath, String type, String child){
		return caches.get(getKey(thingPath, type, child));
	}
	
	public static String getKey(String thingPath, String type, String child){
		return thingPath + "|" + type + "|" + child;
	}
	
	static class CheckExpireTask extends TimerTask{
		public void run(){
			try{
				List<String> removed = new ArrayList<String>();
				for(String key : caches.keySet()){
					ThingRegistCache cache = caches.get(key);
					if(cache != null){
						if(cache.isExpired()){
							removed.add(key);
						}
					}
				}
				
				for(String key : removed){
					caches.remove(key);
				}
			}catch(Exception e){				
			}
		}
	}
	
	static class ReloadTask extends TimerTask{
		public void run(){
			try{
				if(isReloading){
					return;
				}
				
				final List<ThingRegistCache> cachesForReload = new ArrayList<ThingRegistCache>();
				for(String key : caches.keySet()){
					ThingRegistCache cache = caches.get(key);
					if(cache != null){
						if(cache.isNeedReload()){
							cachesForReload.add(cache);
						}
					}
				}
				
				isReloading = true;
				new Thread(new Runnable(){
					public void run(){
						ActionContext ac = new ActionContext();
						for(ThingRegistCache cache : cachesForReload){							
							Map<String, Group> datas = ThingRegistTreeModelActions.getAllChilds(cache.getThingPath(), cache.getRegistType(), cache.getChild(), ac);
							if(datas != null){
								cache.setDatas(datas);
							}
						}
					}
				}).start();
			}catch(Exception e){				
			}
		}
	}
}
