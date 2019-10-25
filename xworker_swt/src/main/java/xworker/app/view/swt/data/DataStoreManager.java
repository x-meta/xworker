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
package xworker.app.view.swt.data;

import java.util.HashMap;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;

/**
 * 全局的DataStore管理器，主要用于注册全局的Store，Store的name属性作为key，注意不能重复，否则会覆盖。
 * 
 * 使用WeakReference保存DataStore。
 * 
 * @author Administrator
 *
 */
public class DataStoreManager {
	private static Map<String, DataStoreEntry> dataStores = new HashMap<String, DataStoreEntry>();
	
	public static Object getDataStore(ActionContext actionContext){
		String dataStoreId = (String) actionContext.get("dataStoreId");
		if(dataStoreId != null && !"".equals(dataStoreId)){
			return get(dataStoreId);
		}else{
			return null;
		}
	}
	
	/**
	 * 注册DataStore。
	 * 
	 * @param dataStore
	 */
	public static void regist(String name, Thing thing, Thing dataStore){
		DataStoreEntry entry = new DataStoreEntry(thing, dataStore);
		dataStores.put(name, entry);
	}
	
	/**
	 * 获取已注册的DataStore，参数为Store定义的路径事物。
	 * 
	 * @param path
	 * @return
	 */
	public static Thing get(String path){
		DataStoreEntry entry = dataStores.get(path);
		Thing dataStoreThing = World.getInstance().getThing(path);
		if(entry != null && dataStoreThing.getMetadata().getLastModified() == entry.lastModified){
			return entry.store;
		}
				
		//尝试把name当做输入的事物路径			
		dataStoreThing = getFistStoreThing(dataStoreThing);
		if(dataStoreThing != null){
			//创建DataStore
			if(UtilData.isTrue(dataStoreThing.doAction("isInstance"))){
				return dataStoreThing;
			}
			
			Thing dataStore = (Thing) dataStoreThing.doAction("create");
			if(dataStore != null){
				regist(path, dataStoreThing, dataStore);
			}
			
			return dataStore;
		}
		
		return null;
	}
	
	/**
	 * 返回数据对象对应的数据仓库，如果不存在那么创建一个。
	 * 
	 * @param dataObjectPath
	 * @return
	 */
	public static Thing getByDataObject(String dataObjectPath) {
		DataStoreEntry entry = dataStores.get(dataObjectPath);
		Thing dataObjectThing = World.getInstance().getThing(dataObjectPath);
		if(dataObjectThing == null) {
			return null;
		}
		
		if(entry != null && dataObjectThing.getMetadata().getLastModified() == entry.lastModified){
			return entry.store;
		}
		
		Thing dataStoreThing = new Thing("xworker.swt.Commons/@DataStore");
		dataStoreThing.initDefaultValue();                      

        //创建数据仓库
		dataStoreThing.put("dataObject", dataObjectPath);
		dataStoreThing.put("autoLoad", "true");
		dataStoreThing.put("attachToParent", "true");
		dataStoreThing.put("loadBackground", "true");
		dataStoreThing.put("labelField", dataObjectThing.getString("displayName"));
		dataStoreThing.getMetadata().setLastModified(dataObjectThing.getMetadata().getLastModified());
        
        Thing dataStore = (Thing) dataStoreThing.doAction("create");
		if(dataStore != null){
			regist(dataObjectPath, dataStoreThing, dataStore);
		}
		
		return dataStore;
	}
	
	/**
	 * 如果Store存在相互引用关系，那么返回最原始的数据仓库的定义。
	 * 
	 * @param store
	 * @return
	 */
	private static Thing getFistStoreThing(Thing store){
		if(store == null){
			return null;
		}
		
		String storeRef = store.getStringBlankAsNull("storeRef");
		if(storeRef != null){
			return getFistStoreThing(World.getInstance().getThing(storeRef));
		}else{
			return store;
		}
	}
	
}