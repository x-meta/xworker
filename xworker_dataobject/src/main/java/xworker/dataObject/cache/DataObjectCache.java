/*******************************************************************************
* Copyright 2007-2013 See AUTHORS file.
 * 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package xworker.dataObject.cache;

import java.util.HashMap;
import java.util.Map;

import org.xmeta.Thing;

import xworker.dataObject.DataObject;

public class DataObjectCache {
	/** 
	 * 缓存设置使用Map。
	 */
	private static Map<String, Map<Object, DataObjectCacheEntry>> caches = new HashMap<String, Map<Object, DataObjectCacheEntry>>();
	
	/**
	 * 线程操作的缓存。
	 */
	private static ThreadLocal<ThreadLocalCache> threadLocalCaches = new ThreadLocal<ThreadLocalCache>();
	
	/**
	 * 获取数据对象缓存。
	 * 
	 * @param dataObjectDescriptor
	 * @param keyName
	 * @param key
	 * @return
	 */
	public synchronized static DataObject getDataObject(Thing dataObjectDescriptor, String keyName, Object key){
		String descriptorPath = dataObjectDescriptor.getMetadata().getPath();
		Map<Object, DataObjectCacheEntry> dataObjectCache = caches.get(descriptorPath);
		if(dataObjectCache == null){
			dataObjectCache = new HashMap<Object, DataObjectCacheEntry>();
			caches.put(descriptorPath, dataObjectCache);
		}
		
		DataObjectCacheEntry entry = dataObjectCache.get(key);
		if(entry != null){
			long timeout = dataObjectDescriptor.getLong("cacheRelationTimeout") ;
			if(timeout <= 0 || System.currentTimeMillis() - entry.cacheTime < timeout * 1000){
				return entry.dataObject;
			}
		}
		
		//重置缓存		
		DataObject dataObject = new DataObject(descriptorPath);
		dataObject.put(keyName, key);
		dataObject.setInited(false);
		
		//保存缓存
		if(dataObjectDescriptor.getInt("cacheRelationMaxSize") <= 0 || dataObjectCache.size() < dataObjectDescriptor.getInt("cacheRelationMaxSize")){
			entry = new DataObjectCacheEntry();
			entry.dataObject = dataObject;
			dataObjectCache.put(key, entry);
		}
		
		return dataObject;
	}
	
	/**
	 * 清除全部缓存。
	 */
	public synchronized static void clearCache(){
		caches.clear();
	}
	
	/**
	 * 清除指定数据对象的缓存。
	 * 
	 * @param descriptor
	 */
	public synchronized static void clearCache(String descriptor){
		Map<Object, DataObjectCacheEntry> dataObjectCache = caches.get(descriptor);
		if(dataObjectCache != null){
			dataObjectCache.clear();
			caches.put(descriptor, null);
		}
	}
	
	/**
	 * 清除指定数据对象的缓存。
	 * 
	 * @param descriptor
	 * @param key
	 */
	public synchronized static void clearCache(String descriptor, Object key){
		Map<Object, DataObjectCacheEntry> dataObjectCache = caches.get(descriptor);
		if(dataObjectCache != null){
			dataObjectCache.remove(key);
		}
	}
	
	/**
	 * 临时开始线程缓存。
	 */
	public static void begin(){
		ThreadLocalCache cache = threadLocalCaches.get();
		if(cache == null){
			cache = new ThreadLocalCache();
			threadLocalCaches.set(cache);
		}
		cache.count++;
	}
	
	/**
	 * 线程缓存结束。
	 */
	public static void finish(){
		ThreadLocalCache cache = threadLocalCaches.get();
		if(cache == null){
			cache = new ThreadLocalCache();
			threadLocalCaches.set(cache);
		}
		cache.count--;
		if(cache.count == 0){
			cache.clearCache();
		}
	}
	
	public synchronized static DataObject getDataObjectThreadCache(Thing dataObjectDescriptor, String keyName, Object key){
		ThreadLocalCache cache = threadLocalCaches.get();
		if(cache == null){
			cache = new ThreadLocalCache();
			threadLocalCaches.set(cache);
		}
		
		return cache.getDataObject(dataObjectDescriptor, keyName, key);
	}
}