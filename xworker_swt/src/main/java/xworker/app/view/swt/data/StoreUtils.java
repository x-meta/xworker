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

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;

import ognl.OgnlException;

/**
 * 由于Store最早是用事物保存到的，有些参数名比较模糊，用这个方法可以简化。
 * 
 * @author Administrator
 *
 */
public class StoreUtils {	
	/**
	 * 返回一个数据仓库数据是否已经装载。
	 * 
	 * @param store
	 * @return
	 */
	public static boolean isLoaded(Thing store){
		return store.getBoolean("dataLoaded");
	}
	
	/**
	 * 设置数据仓库的装载状态。
	 * 
	 * @param store
	 * @param loaded
	 */
	public static void setLoaded(Thing store, boolean loaded){
		store.put("dataLoaded", loaded);
	}
	
	/**
	 * SWT控件中常用的获取store的方法。
	 * 默认从dataStore属性获取，其次从DataStors子节点获取。
	 * 
	 * @param actionContext
	 * @return
	 * @throws OgnlException 
	 */
	public static Thing getDataStore(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		//首先尝试从属性中获取
		if(self.getStringBlankAsNull("dataStore") != null){
			Object obj = UtilData.getData(self, "dataStore", actionContext);
			if(obj instanceof Thing){
				return (Thing) obj;
			}else if(obj instanceof String){
				Thing store = World.getInstance().getThing((String) obj);
				if(store != null){
					return (Thing) store.doAction("create", actionContext);
				}else{
					throw new ActionException("DataStore thing not exists, storePath=" + obj + ", path=" + self.getMetadata().getPath());
				}
			}
		}
		
		//尝试从子节点中获取
		Thing dataStores = self.getThing("DataStores@0");
		if(dataStores != null && dataStores.getChilds().size() > 0){
			Thing store = dataStores.getChilds().get(0);
			return (Thing) store.doAction("create", actionContext);
		}else{
			return null;
		}
	}
}