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

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

public class BeginCreator {
    @SuppressWarnings("unchecked")
	public static Object run(ActionContext actionContext){
        Thing self = (Thing) actionContext.get("self");
        
        //self变量改名
        if(self.getBoolean("changeSelf")){  
            List<Thing> things = actionContext.getThings();
            Thing s = null;
            if(things.size() > 1){
                s = things.get(things.size() - 2);
            }
            actionContext.peek().put(self.getString("selfVarName"), s);
        }
        
        Object result = null;
        Bindings bindings = actionContext.peek();
        bindings.setVarScopeFlag(); //设置局部变量范围的标识
        //现在Variables功能已经集成到引擎中，下面的代码如果没问题可以删除了
        /*
        for(Thing vars : self.getChilds("Variables")){
        	for(Thing var : vars.getChilds()){
        		String key = var.getMetadata().getName();
        		Object value = var.getAction().run(actionContext, null, true);
        		bindings.put(key, value);
        	}
        }*/
        
        //执行子动作
        for(Thing actions :(List<Thing>) self.get("actions@")){
            //log.info("Action: " + actions);
            for(Thing action : actions.getChilds()){      
                result = action.getAction().run(actionContext, null, true);
        
                if(ActionContext.RETURN == actionContext.getStatus() || 
                    ActionContext.CANCEL == actionContext.getStatus() || 
                    ActionContext.BREAK == actionContext.getStatus() || 
                    ActionContext.EXCEPTION == actionContext.getStatus() ||
                    ActionContext.CONTINUE == actionContext.getStatus()){
                    break;
                }
            } 
            if(ActionContext.RETURN == actionContext.getStatus() || 
                ActionContext.CANCEL == actionContext.getStatus() || 
                ActionContext.BREAK == actionContext.getStatus() || 
                ActionContext.EXCEPTION == actionContext.getStatus() ||
                ActionContext.CONTINUE == actionContext.getStatus()){
                break;
            }
        }
        
        return result;
    }

}