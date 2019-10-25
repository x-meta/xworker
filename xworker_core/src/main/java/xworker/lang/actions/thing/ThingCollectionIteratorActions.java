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

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class ThingCollectionIteratorActions {
	//private static Logger logger = LoggerFactory.getLogger(ThingCollectionIteratorActions.class);
	
	public static Object run(ActionContext actionContext){
		World world = World.getInstance();
		Thing self = (Thing) actionContext.get("self");
		
		Object result = null;
		String thingPaths = self.getString("thingPaths");
		String excludePaths = self.getString("excludePaths");
		
		Thing childAction = self.getThing("ChildActions@0");
		if(childAction != null && childAction.getChilds().size() > 0){
			ThingIterator iter = new ThingIterator(self, thingPaths);
			String thingVarName = self.getString("varName");
			
			int index = 0;
			while(iter.hasNext()){
				Thing thing = iter.next();
				
				if(excludePaths != null && !"".equals(excludePaths)){
					boolean exclude = false;
					for(String exPath : excludePaths.split("[,]")){
						if(thing.getMetadata().getPath().startsWith(exPath.trim())){
							exclude = true;
							break;
						}
					}
					if(exclude){
						continue;
					}
				}
				actionContext.peek().put(thingVarName, thing);
				actionContext.peek().put(thingVarName + "_index", index);
				actionContext.peek().put(thingVarName + "_hasNext", iter.hasNext());
				index++;
				
				for(Thing child : childAction.getChilds()){
					Action action = world.getAction(child);
					if (action != null) {
						result = action.run(actionContext, null, true);
					}

					//判断循环的状态
					if (actionContext.getStatus() == ActionContext.BREAK) {
						actionContext.setStatus(ActionContext.RUNNING);
						break;
					} else if (actionContext.getStatus() == ActionContext.CONTINUE) {
						actionContext.setStatus(ActionContext.RUNNING);
						continue;
					} else if (actionContext.getStatus() != ActionContext.RUNNING) {
						break;
					}
				}
			}
		}
		
		return result;
	}
}