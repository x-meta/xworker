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
package xworker.lang.flow;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingFlowCreator {
    public static void runInSwt(ActionContext actionContext){
        ActionContext context = new ActionContext();
        //ActionContext explorerContext = (ActionContext) actionContext.get("explorerContext");
        context.put("explorerActions", actionContext.get("explorerActions"));
        context.put("explorerContext", actionContext.get("explorerContext"));
        context.put("thingFlowListener", ((ActionContext) actionContext.get("explorerContext")).get("thingFlowListener"));
        //context.put("display", ((Shell) explorerContext.get("shell")).getDisplay());
        		
        ((Thing) actionContext.get("currentThing")).doAction("run", context);
    }

}