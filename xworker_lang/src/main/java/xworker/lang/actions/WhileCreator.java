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

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.util.UtilData;

public class WhileCreator {
	static World world = World.getInstance();
    
	public static Object run(ActionContext actionContext) throws Exception{
        Thing self = (Thing) actionContext.get("self");
        
        actionContext.peek().setVarScopeFlag();//.isVarScopeFlag = true;
        Object result = null;
        
        Thing childActions = self.getThing("ChildAction@0");
        if(childActions == null) {
        	//新的版本
        	ActionsFilter actions = new ActionsFilter(self.getChilds(), "condition");
        	while(actions.exists("condition") && UtilData.isTrue(actions.run("condition", actionContext, false))) {
        		result = actions.runList(actionContext, true);
        	}
        }else {
	        while(IfCreator.checkCondition(self, actionContext)){
	        	 
	        	for (Thing child : childActions.getChilds()) {
					Action action = world.getAction(child);
					if (action != null) {
						result = action.run(actionContext, null, true);
					}
	
					int sint = actionContext.getStatus();
					if (sint != ActionContext.RUNNING) {
						break;
					}
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
            
        return result;      
    }

}