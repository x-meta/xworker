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
package xworker.listeners;

import java.util.List;

import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.ThingManagerListener;

public class SwtMenuListener implements ThingManagerListener{
	private static SwtMenuListener instance;
    //菜单缓存
    private final DataMenuCache menuCache = new DataMenuCache();

	private SwtMenuListener(){				
		menuCache.thingMenuName = "sswtMenus";
	}
	
	public static SwtMenuListener getInstance(){
		if(instance == null){
			instance = new SwtMenuListener();
		}
		
		return instance;
	}

	/**
     * 取得菜单。
     */
	public List<Thing> getMenu(Thing thing, String[] name, String type){    	
    	return menuCache.getMenu(thing, name, type);
    }
    
	public List<Thing> getMenu(Thing thing, List<String> names, String type){
    	String[] strs = new String[names.size()];
    	for(int i=0; i<names.size(); i++){
    		strs[i] = names.get(i);
    	}
    	return getMenu(thing, strs, type);
    }

    /**
     * 使用菜单事物主动更新缓存。
     */
    public void updateMenu(Thing menuThing){
		if("xworker.ide.config.ProjectMenuSwt".equals(menuThing.getMetadata().getPath())){
			//使用新的菜单，就得模型公共菜单取消了
			return;
		}

    	menuCache.update(menuThing);
    }
    
	public void loaded(ThingManager thingManager, Thing thing) {
    	if("xworker.ide.config.ProjectMenuSwt".equals(thing.getMetadata().getPath())){
    		//使用新的菜单，就得模型公共菜单取消了
    		return;
		}

		if("projectMenuSwt".equals(thing.getRoot().getMetadata().getName())){
        	menuCache.update(thing.getRoot());
        	menuCache.checkCache();
        }
		
		//项目事物索引
		//ProjectThingIndexThread.updateIndex(thing);
	}

	public void removed(ThingManager thingManager, Thing thing) {		
		//项目事物索引
		//ProjectThingIndexThread.removeIndex(thing);
	}

	public void saved(ThingManager thingManager, Thing thing) {
		loaded(thingManager, thing);
		
		//项目事物索引
		//ProjectThingIndexThread.updateIndex(thing);
	}
}