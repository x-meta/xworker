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
package xworker.lang.actions;

import java.util.HashMap;
import java.util.Map;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.lang.executor.Executor;

public class ActionMenuCreator {
	private static final String TAG = ActionMenuCreator.class.getName();
	
    public static void runAction(ActionContext actionContext){
        //println currentThing.doAction("run", actionContext);
    	Thing currentThing = (Thing) actionContext.get("currentThing");
        Action action = currentThing.getAction();
        Object result = action.run(actionContext);
        System.out.println(result);
    }

    public static void viewCodeClassPath(ActionContext actionContext){
        World world = World.getInstance();
        ActionContext explorerContext = (ActionContext) actionContext.get("explorerContext");
        Thing currentThing = (Thing) actionContext.get("currentThing");
        
        Thing shellThing = world.getThing("xworker.ide.worldexplorer.swt.dialogs.ActionInfoDialog");
        ActionContext ac = new ActionContext();
        Action action = world.getAction(currentThing);
        ac.put("parent", explorerContext.get("shell"));
        //ac.put("title", "动作代码和类路径");
        ac.put("text", "代码路径：" + action.getFileName() + "\n类路径：" + action.getClassFileName() + "\n类：" + action.getClassName());
        
        shellThing.doAction("run", ac);
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("codePath", action.getFileName());
        values.put("classPath", action.getClassFileName());
        values.put("className", action.getClassName());
        
        Thing thingForm = ac.getObject("thingForm");
        thingForm.doAction("setValues", actionContext, "values", values);
    }

    public static void setActionChanged(ActionContext actionContext){
        Thing currentThing = (Thing) actionContext.get("currentThing");
        
        Action action = currentThing.getAction();
        action.setChanged(true);
        
        Executor.info(TAG, "action " + currentThing.getMetadata().getPath() + " has seted to changed");
    }

}