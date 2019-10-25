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
package xworker.java.swing;

import javax.swing.Action;
import javax.swing.Icon;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ActionCreator {
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		String name = self.getMetadata().getName();
		Icon icon = SwingCreator.createIcon(self, "icon", actionContext);
		
		Action action = null;
		if(icon != null){
			action = new ThingAction(self, actionContext, name, icon);
		}else{
			action = new ThingAction(self, actionContext, name);
		}
		
		actionContext.getScope(0).put(name, action);
		return action;
	}
}