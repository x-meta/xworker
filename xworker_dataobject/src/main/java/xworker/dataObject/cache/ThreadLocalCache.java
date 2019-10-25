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

public class ThreadLocalCache {
	protected Map<String, Map<Object, DataObjectCacheEntry>> caches = new HashMap<String, Map<Object, DataObjectCacheEntry>>();
	
	protected int count;
	
	/**
	 * 获取数据对象缓存。
	 * 
	 * @param dataObjectDescriptor
	 * @param keyName
	 * @param key
	 * @return
	 */
	public DataObject getDataObject(Thing dataObjectDescriptor, String keyName, Object key){
		//null应该不能作为键值而存在
		if(key == null) {
			return null;
		}
		
		if(dataObjectDescriptor.getBoolean("cacheRelationReadnone")){
			DataObject dataObject = new DataObject(dataObjectDescriptor.getMetadata().getPath());
			dataObject.put(keyName, key);
			dataObject.setInited(false);
			return dataObject;
		}else{		
			String descriptorPath = dataObjectDescriptor.getMetadata().getPath();
			Map<Object, DataObjectCacheEntry> dataObjectCache = caches.get(descriptorPath);
			if(dataObjectCache == null){
				dataObjectCache = new HashMap<Object, DataObjectCacheEntry>();
				caches.put(descriptorPath, dataObjectCache);
			}
			
			DataObjectCacheEntry entry = dataObjectCache.get(key);
			if(entry != null){
				return entry.dataObject;
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
	}
	
	public void clearCache(){
		caches.clear();
	}
}