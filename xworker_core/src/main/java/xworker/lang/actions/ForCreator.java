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

import java.util.ArrayList;
import java.util.List;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;

import xworker.util.UtilAction;

public class ForCreator {
	static World world = World.getInstance();
    public static Object run(ActionContext actionContext){
        Thing self = (Thing) actionContext.get("self");
        
        //String initCode = self.getString("init");
        //String conditionCode = self.getString("condition");
        //String controlCode = self.getString("control");        
        Bindings bindings = actionContext.push(null);  
        bindings.setVarScopeFlag();//.isVarScopeFlag = true; //局部变量范围
        Thing childActions = self.getThing("ChildAction@0"); 
        
        try{            
	        if(childActions == null) {
	        	//新的版本
	        	Object result = null;
	        	Thing init  = null;
	        	Thing condition = null;
	        	Thing control = null;
	        	List<Thing> childs = new ArrayList<Thing>();
	        	for(Thing child : self.getChilds()) {
	        		String name = child.getMetadata().getName();
	        		if("init".equals(name)) {
	        			init = child;
	        		}else if("condition".equals(name)) {
	        			condition = child;
	        		}else if("control".equals(name)) {
	        			control = child;
	        		}else {
	        			childs.add(child);
	        		}
	        	}
	        	
	        	if(init != null) {
	        		init.getAction().run(actionContext, null, true);
	        	}
	        	if(condition != null) {
	        		while(UtilData.isTrue(condition.getAction().run(actionContext, null, false))) {
	        			result = UtilAction.runChildActions(childs, actionContext, true);
	        			
	        			//循环控制处理
		                if(actionContext.getStatus() == ActionContext.BREAK){
		                    actionContext.setStatus(ActionContext.RUNNING);
		                    break;
		                }else if(actionContext.getStatus() != ActionContext.RUNNING){
		              	    break;
		                }
		                
		                if(control != null) {
		                	control.getAction().run(actionContext, null, true);
		                }
		                
		                if(actionContext.getStatus() == ActionContext.CONTINUE){
		                    actionContext.setStatus(ActionContext.RUNNING);
		                    continue;
		                }
	        		}
	        	}
	        	
	        	return result;
	        }else {
	            //初始化，相当于for(int i=0; i<10; i++)中的int i=0;
	        	self.doAction("init", actionContext, null, true);
	            
	            Object result = null;
	            //循环条件，相当于i<10;            
	            while((Boolean) self.doAction("condition", actionContext, null, true)){           
	                for(Thing child : childActions.getChilds()){
	                    Action action = world.getAction(child);
	                    if(action != null){
	                        result = action.run(actionContext, null, true);
	                    }      
	                 
	        		    int sint = actionContext.getStatus();
	    	    	    if(sint != ActionContext.RUNNING){
	    				    break;
	    			    }
	                }
	                
	                //循环控制处理
	                if(actionContext.getStatus() == ActionContext.BREAK){
	                    actionContext.setStatus(ActionContext.RUNNING);
	                    break;
	                }else if(actionContext.getStatus() == ActionContext.CONTINUE){
	                    actionContext.setStatus(ActionContext.RUNNING);
	                    continue;
	                }else if(actionContext.getStatus() != ActionContext.RUNNING){
	              	    break;
	                }
	                
	                //控制语句，相当于i++
	                self.doAction("control", actionContext, null, true);
	            }
	            
	            return result;
	        }
        }finally{
            actionContext.pop();
        }        
    }

}