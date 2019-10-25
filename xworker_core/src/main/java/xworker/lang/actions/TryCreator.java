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

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

public class TryCreator {
	static World world = World.getInstance();
    
	public static void run(ActionContext actionContext) throws Throwable{
        Thing self = (Thing) actionContext.get("self");        

        List<Thing> catchList = self.getChilds("Catch");
        Thing finallyObj = self.getThing("Finally@0");
        
        actionContext.peek().setVarScopeFlag();
        boolean catched = false;
    	try{
    		Thing childs = self.getThing("ChildAction@0");
    		if(childs == null){
    			return;
    		}
    		
    		for(Thing child : childs.getChilds()){
    			Action action = world.getAction(child);
                if(action != null){
                    action.run(actionContext, null, true);
                }      
                                    
				int sint = actionContext.getStatus();
				if(sint != ActionContext.RUNNING){
					break;
				}
			}
			
    		if(actionContext.getStatus() == ActionContext.EXCEPTION){
    			Object throwed = actionContext.getThrowedObject();
    			catched = doCatch(catchList, throwed, actionContext);
    			if(catched){
    				actionContext.setStatus(ActionContext.RUNNING);
    			}
    		}
    	}catch(Throwable e){     
    		catched = doCatch(catchList, e, actionContext);        
    	  
    		if(!catched){
    			throw e;
    		}
    	}finally{
    		if(finallyObj != null){
    			for(Thing child : finallyObj.getChilds()){
					Action action = world.getAction(child);
                    if(action != null){
                        action.run(actionContext);
                    }      
                    
                    if(actionContext.getStatus() != ActionContext.RUNNING){
                    	break;
                    }        				
				}
    		}
    	}          
    }
	
	public static boolean doCatch(List<Thing> catchList, Object throwed, ActionContext actionContext){
		boolean catched = false;
		for(Thing catchObj : catchList){       		    
			String exception = catchObj.getString("exception"); 
			String varName = catchObj.getString("varName");            			
			Class<?> exceptionClass = null;
			try{
				exceptionClass = Class.forName(exception);
			}catch(Exception ee){
				
			}
			if(exceptionClass != null && exceptionClass.isInstance(throwed)){
				catched = true;
				
				actionContext.setStatus(ActionContext.RUNNING);
				actionContext.setThrowedObject(null);
				
				Bindings bindings = actionContext.push(null);
				try{
    				bindings.put(varName, throwed);
    				
    				for(Thing child : catchObj.getChilds()){
    					Action action = world.getAction(child);
                        if(action != null){
                            action.run(actionContext, null, true);
                        }      
                        					
                        if(actionContext.getStatus() != ActionContext.RUNNING){
                        	break;
                        } 
    				}
				}finally{
					actionContext.pop();
				}
				
				break;
			}        			
		}
		
		return catched;
	}

}