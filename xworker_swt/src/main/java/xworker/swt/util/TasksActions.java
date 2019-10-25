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
package xworker.swt.util;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

/**
 * 一些具体任务相关的动作。
 * 
 * @author Administrator
 *
 */
public class TasksActions {
	public static void sleep(ActionContext actionContext) throws InterruptedException{
		Thing self = (Thing) actionContext.get("self");
		long time = self.getLong("time");
		if(time > 0){
			Thread.sleep(time);
		}
	}
	
	public static void loadThing(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		World.getInstance().getThing(self.getString("thingPath"));
	}
	
	public static void thingExecutor(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Thing thing = World.getInstance().getThing(self.getString("thingPath"));
		if(thing != null){
			String action = self.getStringBlankAsNull("actionName");
			if(action == null){
				action = "run";
			}
			
			thing.doAction(action, actionContext);
		}
	}
}