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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingAction {
	private static Logger logger = LoggerFactory.getLogger(ThingAction.class);
	
	/**
	 * 执行指定事物的动作。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Object run(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//String thingPath = (String) self.doAction("getThingPath", actionContext);
		Thing thing = (Thing) self.doAction("getThing", actionContext);
		if(thing == null){
			logger.info("thing is null, action=" + self.getMetadata().getPath());			
			return null;
		}
		
		String action = (String) self.doAction("getActionName", actionContext);
		if(action == null){
			action = "run";
		}
		
		return thing.doAction(action, actionContext);
		
	}
}