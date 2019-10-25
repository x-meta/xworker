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

/**
 * 用于注册到TaskMonitor下的任务。
 * 
 * @author Administrator
 *
 */
public class DataStoreTasks {
	/**
	 * 装载数据仓库的数据。
	 * 
	 * @param actionContext
	 */
	public static void loadTask(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String storePath = self.getStringBlankAsNull("dataStorePath");
		if(storePath != null){
			Thing store = DataStoreManager.get(storePath);			
			if(store != null){
				if(!StoreUtils.isLoaded(store)){
					store.doAction("load");
					long time = System.currentTimeMillis();
					long timeOut = self.getLong("timeOut");
					while(!StoreUtils.isLoaded(store)){
						//进入此的通常都是异步装载
						try {
							Thread.sleep(30);
						} catch (InterruptedException e) {
						}
						
						if(timeOut > 0 && System.currentTimeMillis() - time > timeOut){
							if(self.getBoolean("exceptionOnTimeOut")){
								throw new ActionException("Load datastore timeout, task=" + self.getMetadata().getPath());
							}else{
								break;
							}
						}
					}
				}
			}
		}
	}
}