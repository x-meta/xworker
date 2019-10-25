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
package xworker.lang.actions.thing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Category;
import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;
import org.xmeta.util.UtilAction;

public class ThingCollectionsActions {
	private static Logger logger = LoggerFactory.getLogger(ThingCollectionsActions.class);
	
	public static Object run(ActionContext actionContext){
		World world = World.getInstance();
		Thing self = (Thing) actionContext.get("self");
		
		List<Thing> things = new ArrayList<Thing>();
		String thingPaths = self.getString("thingPaths");
		if(thingPaths != null && !"".equals(thingPaths)){
			for(String path : thingPaths.split("[,]")){
				path = path.trim();
				if("".equals(path)){
					continue;
				}
				
				if(path.indexOf(":") != -1){
					getThingManagerThings(self, path.substring(0, path.indexOf(":")), path.substring(path.indexOf(":") + 1, path.length()), things);
				}else{
					Object obj = world.get(path);
					if(obj == null){
						logger.info("ThingCollections: the object of path is null, path=" + path + ",thing=" + self.getMetadata().getPath());
					}else if(obj instanceof Category){
						Iterator<Thing> iter = ((Category) obj).iterator(true);
						while(iter.hasNext()){
							things.add(iter.next());
						}
					}else if(obj instanceof Thing){
						things.add((Thing) obj);
					}
				}
			}
		}
		
		//保存变量
		UtilAction.putVarByActioScope(self, self.getString("varName"), things, actionContext);
		
		return things;		
	}
	
	public static void getThingManagerThings(Thing self, String thingManagerName, String path, List<Thing> things){
		World world = World.getInstance();
		ThingManager thingManager = world.getThingManager(thingManagerName);
		if(thingManager == null){
			logger.info("ThingCollections: thingManager is null, name=" + thingManagerName + ",thing=" + self.getMetadata().getPath());
		}else{
			Category category = thingManager.getCategory(path);
			if(category != null){
				Iterator<Thing> iter = category.iterator(true);
				while(iter.hasNext()){
					things.add(iter.next());
				}
			}else{
				Thing thing = thingManager.getThing(path);
				if(thing != null){
					things.add(thing);
				}
			}
		}
	}
}