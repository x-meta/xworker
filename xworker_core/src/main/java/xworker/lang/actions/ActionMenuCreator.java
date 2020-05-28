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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class ActionMenuCreator {
	private static Logger log = LoggerFactory.getLogger(ActionMenuCreator.class);
	
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
        
        Thing shellThing = world.getThing("xworker.ide.worldexplorer.swt.dialogs.TextDialog/@shell");
        ActionContext ac = new ActionContext();
        Action action = world.getAction(currentThing);
        ac.put("parent", explorerContext.get("shell"));
        ac.put("title", "动作代码和类路径");
        ac.put("text", "代码路径：" + action.fileName + "\n类路径：" + action.classFileName + "\n类：" + action.className);
        
        shellThing.doAction("run", ac);
    }

    public static void setActionChanged(ActionContext actionContext){
        Thing currentThing = (Thing) actionContext.get("currentThing");
        
        Action action = currentThing.getAction();
        action.changed = true;
        
        log.info("action " + currentThing.getMetadata().getPath() + " has seted to changed");
    }

}