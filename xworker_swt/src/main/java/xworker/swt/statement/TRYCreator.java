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
package xworker.swt.statement;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Bindings;
import org.xmeta.Thing;

public class TRYCreator {
    @SuppressWarnings({ "rawtypes" })
	public static Object create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");        

        List<Thing> catchList = self.getChilds("Catch");
        Thing finallyObj = self.getThing("Finally@0");
        
        Object result = "success";
        try{
        	try{
        		for(Thing child : self.getAllChilds()){
        			if(child.isThingByName("Catch") || child.isThingByName("Finally")){
        				continue;
        			}
        			
    				result = child.doAction("create", actionContext, null, true);     
                                        
    				int sint = actionContext.getStatus();
    				if(sint != ActionContext.RUNNING){
    					break;
    				}
    			}
    			
        		if(actionContext.getStatus() == ActionContext.EXCEPTION){
        			Object throwed = actionContext.getThrowedObject();
        			//System.out.println("catched throwed=" + throwed);
            		for(Thing catchObj : catchList){       		    
            			String exception = catchObj.getString("exception"); 
            			String varName = catchObj.getString("varName");   
            			
            			Class exceptionClass = null;
            			try{
            				exceptionClass = Class.forName(exception);
            			}catch(Exception ee){
            				
            			}
            			if(exceptionClass != null && exceptionClass.isInstance(throwed)){
            				actionContext.setStatus(ActionContext.RUNNING);
            				actionContext.setThrowedObject(null);
            				
            				Bindings bindings = actionContext.push(null);
            				bindings.put(varName, throwed);
            				
            				for(Thing child : catchObj.getAllChilds()){
            					result = child.doAction("create", actionContext, null, true);   
                                					
                                if(actionContext.getStatus() != ActionContext.RUNNING){
                                	break;
                                } 
            				}
            				
            				break;
            			}        			
            		}
        		}
        		
    			return result;
        	}catch(Throwable e){        	    
        	   //e.printStackTrace();
        		boolean cached = false;
        		for(Thing catchObj : catchList){       		    
        			String exception = catchObj.getString("exception");   
        			String varName = catchObj.getString("varName");
        			Class exceptionClass = null;
        			try{
        				exceptionClass = Class.forName(exception);
        			}catch(Exception ee){
        				
        			}
        			if(exceptionClass != null && exceptionClass.isInstance(e)){
        			    actionContext.setStatus(ActionContext.RUNNING);
          				actionContext.setThrowedObject(null);
        				cached = true;
        				Bindings bindings = actionContext.push(null);
        				bindings.put(varName, e);
        				for(Thing child : catchObj.getAllChilds()){
        					result = child.doAction("create", actionContext, null, true);
                            					
                            if(actionContext.getStatus() != ActionContext.RUNNING){
                            	break;
                            } 
        				}
        				
        				break;
        			}        			
        		}
        		
        		if(!cached){
        			if(e instanceof RuntimeException){
        				throw (RuntimeException) e;
        			}else{
        				throw new ActionException("", e);
        			}
        		}
        	}finally{
        		if(finallyObj != null){
        			for(Thing child : finallyObj.getAllChilds()){
    					result = child.doAction("create", actionContext, null, true);     
                        
                        if(actionContext.getStatus() != ActionContext.RUNNING){
                        	break;
                        }        				
    				}
        		}
        	}
        	
            return result;
        }finally{            
            //map.pop();
        }        
    }

}