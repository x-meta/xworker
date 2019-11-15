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

import ognl.OgnlException;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilData;

public class IfCreator {
	static World world = World.getInstance();
	
	public static boolean checkCondition(Thing action, ActionContext actionContext) throws OgnlException{
		String condition = action.getStringBlankAsNull("condition");
		if(condition != null){
			return UtilData.isTrue(OgnlUtil.getValue(action, "condition", condition, actionContext));
		}else{
			return UtilData.isTrue(action.doAction("condition", actionContext, null, true));
		}
	}
	
	public static boolean condition(ActionContext actionContext) throws OgnlException{
		Thing self = actionContext.getObject("self");
		String condition = self.getStringBlankAsNull("condition");
		if(condition != null){
			return UtilData.isTrue(OgnlUtil.getValue(self, "condition", condition, actionContext));
		}else{
			return true;
		}
	}
	
    public static Object run(ActionContext actionContext) throws Exception{
        Thing self = (Thing) actionContext.get("self");

        List<Thing> statements = null;
        actionContext.peek().setVarScopeFlag();//.isVarScopeFlag = true; //一个新的局部变量范围
                
        Thing childActions = self.getThing("Then@0"); 
        Thing elseObj = self.getThing("Else@0");
                        
        if(childActions == null && elseObj == null ) {
        	//版本2修改了IF模型的规则
        	Thing condition = null;
        	Thing then = null;
        	Thing elset = null;
        	for(Thing child : self.getChilds()) {
        		if("condition".equals(child.getMetadata().getName().toLowerCase())) {
        			condition = child;
        		}else if("then".equals(child.getMetadata().getName().toLowerCase())) {
        			then = child;
        		}else if("else".equals(child.getMetadata().getName().toLowerCase())) {
        			elset = child;
        		}
        	}
        	
        	if(condition != null && UtilData.isTrue(condition.getAction().run(actionContext, null, false))) {
        		if(then != null) {
        			return then.getAction().run(actionContext, null, true);
        		}
        	}else if(elset != null) {
        		return elset.getAction().run(actionContext, null, true);
        	}
        	
        	return null;
        }else {
        	//旧版的代码
	        if(UtilData.isTrue(self.doAction("condition", actionContext, null, false))){
	    		if(childActions != null){
	    			statements = childActions.getChilds();
	    		}
	        }else{
	        	if(elseObj != null){
	    			statements = elseObj.getChilds();
	    		}
	        }
	        Object o = new Object();
	        o.toString();
	                       	
	    	if(statements != null){
	    		Object result = null;
	    		for(Thing child : statements){
	                Action action = world.getAction(child);
	                if(action != null){
	                    result = action.run(actionContext, null, true);
	                }      
	                
					if(actionContext.getStatus() != ActionContext.RUNNING){
						break;
					}
	            }
	    		
	    		return result;
	    	}else{
	    		return null;
	    	}
        }
    }
}