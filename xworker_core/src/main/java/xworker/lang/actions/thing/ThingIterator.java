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

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Category;
import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;

@SuppressWarnings("unchecked")
public class ThingIterator implements Iterator {
	private static Logger logger = LoggerFactory.getLogger(ThingIterator.class);
	
	World world = World.getInstance();
	
	Thing self = null;
	String[] paths = null;
	
	private boolean hasNext = false;
	
	Iterator<Thing> currentIter = null;
	Thing nextThing = null;
	int pathIndex = 0;
	
	public ThingIterator(Thing self, String paths){
		this.self = self;
		this.paths = paths.split("[,]");
		initNext();
	}

	private void initNext(){
		if(currentIter != null && currentIter.hasNext()){
			hasNext = true;
			return;
		}
				
		while(true){			
			if(pathIndex >= paths.length){
				hasNext = false;
				return;
			}
			
			String path = paths[pathIndex].trim();
			pathIndex++;
			if("".equals(path)){				
				continue;
			}
			
			if(path.indexOf(":") != -1){
				String thingManagerName = path.substring(0, path.indexOf(":"));
				path = path.substring(path.indexOf(":") + 1, path.length());
				
				ThingManager thingManager = world.getThingManager(thingManagerName);
				if(thingManager == null){					
					logger.info("ThingCollectionIterator: thingManager is null, name=" + thingManagerName + ",thing=" + self.getMetadata().getPath());
					continue;
				}else{
					Category category = thingManager.getCategory(path);
					if(category != null){
						Iterator<Thing> iter = category.iterator(true);
						if(iter.hasNext()){
							hasNext = true;
							currentIter = iter;
							return;
						}
					}else{
						Thing thing = thingManager.getThing(path);
						if(thing != null){
							currentIter = null;
							nextThing = thing;
							hasNext = true;
							return;
						}
					}
				}
			}else{
				Object obj = world.get(path);
				if(obj == null){
					logger.info("ThingCollections: the object of path is null, path=" + path + ",thing=" + self.getMetadata().getPath());
					continue;
				}else if(obj instanceof Category){
					Iterator<Thing> iter = ((Category) obj).iterator(true);
					if(iter.hasNext()){
						hasNext = true;
						nextThing = null;
						currentIter = iter;
						return;
					}
				}else if(obj instanceof Thing){
					currentIter = null;
					nextThing = (Thing) obj;
					hasNext = true;
					return;
				}
			}
		}
	}
	
	@Override
	public boolean hasNext() {
		return hasNext;
	}

	@Override
	public Thing next() {
		if(hasNext){
			Thing thing = null;
			if(currentIter != null){
				thing = currentIter.next();
			}else{
				thing = nextThing;
				nextThing = null;
			}
			
			initNext();
			return thing;
		}else{
			return null;
		}
	}

	@Override
	public void remove() {
	}	
}