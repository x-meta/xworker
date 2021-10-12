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
import xworker.dataObject.utils.DataObjectLabelUtils;

public class ThreadLocalCache {
	protected Map<String, Map<Object, DataObjectCacheEntry>> caches = new HashMap<>();
	protected Map<String, DataObjectLabelUtils> labelUtilsMap = new HashMap<>();
	
	protected int count;

	/**
	 * 获取数据对象标签工具，如果不存在创建一个。
	 */
	public DataObjectLabelUtils getDataObjectLabelUtils(Thing dataObject){
		String path = dataObject.getMetadata().getPath();
		return labelUtilsMap.computeIfAbsent(path, k-> new DataObjectLabelUtils(dataObject));
	}

	/**
	 * 获取数据对象缓存。
	 * 
	 */
	public DataObject getDataObject(Thing dataObjectDesc, String keyName, Object key){
		//null应该不能作为键值而存在
		if(key == null) {
			return null;
		}
		
		if(dataObjectDesc.getBoolean("cacheRelationReadnone")){
			DataObject dataObject = new DataObject(dataObjectDesc.getMetadata().getPath());
			dataObject.put(keyName, key);
			dataObject.setInited(false);
			return dataObject;
		}else{		
			String descriptorPath = dataObjectDesc.getMetadata().getPath();
			Map<Object, DataObjectCacheEntry> dataObjectCache = caches.computeIfAbsent(descriptorPath, k -> new HashMap<>());

			DataObjectCacheEntry entry = dataObjectCache.get(key);
			if(entry != null){
				return entry.dataObject;
			}
			
			//重置缓存		
			DataObject dataObject = new DataObject(descriptorPath);
			dataObject.put(keyName, key);
			dataObject.setInited(false);
			
			//保存缓存
			if(dataObject.getInt("cacheRelationMaxSize") <= 0 || dataObjectCache.size() < dataObject.getInt("cacheRelationMaxSize")){
				entry = new DataObjectCacheEntry();
				entry.dataObject = dataObject;
				dataObjectCache.put(key, entry);
			}
			
			return dataObject;
		}
	}
	
	public void clearCache(){
		caches.clear();
		labelUtilsMap.clear();
	}
}