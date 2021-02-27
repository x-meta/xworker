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
package xworker.app.view.swt.app.workbentch.actions;

import org.eclipse.swt.widgets.Event;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.lang.executor.Executor;
import xworker.swt.ActionContainer;

public class SwtNavigationTreeActionTypeActionCreator {
	private static final String TAG = SwtNavigationTreeActionTypeActionCreator.class.getName();
	
    public static void run(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	ActionContainer workbentchActions = (ActionContainer) actionContext.get("workbentchActions"); 
    	Object data = actionContext.get("data");
    	Event event = (Event) actionContext.get("event");
    	
        if("openEditor".equals(self.getString("method"))){
            workbentchActions.doAction("openEditor", UtilMap.toMap(new Object[]{"name", self.get("value"), "data", data, "params", UtilMap.toMap(new Object[]{"item",event.item})}));
        }else{
            Action action = World.getInstance().getAction(self.getString("value"));
            if(action != null){
                action.run(actionContext, UtilMap.toMap(new Object[]{"data", data}));
            }else{
                Executor.info(TAG, "NavigationTreeAction-TypeAciton: action not exits, action=" + self.get("value"));
            }
        }
    }

}