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
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;

public class SwitchCreator {
	static World world = World.getInstance();
    
	public static Object run(ActionContext actionContext) throws Exception{
        Thing self = (Thing) actionContext.get("self");
        
        Object switchValue = UtilData.getData(self, "switchValue", actionContext);
        
        List<Thing> caseList = self.getChilds("Case");
        Thing defaultObj = self.getThing("Default@0");
        
        actionContext.peek().setVarScopeFlag();//.isVarScopeFlag = true;
        Object result = "success";
        try{
        	//判断表达式
        	boolean caseBreak  = false;
        	//是否已经匹配到了一个case
        	boolean caseOk = false;
            for(Thing caseObj : caseList){
            	//和Java规则一样，匹配到一个case后，后续的case也执行
            	if(!caseOk){
            		Object caseValue = UtilData.getData(caseObj, "caseValue", actionContext);
                	if(switchValue == caseValue || (switchValue != null && switchValue.equals(caseValue))){
                		caseOk = true;
                	}
            	}
            	
            	if(caseOk){
            		 for(Thing child : caseObj.getChilds()){
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
                     if(actionContext.getStatus() != ActionContext.RUNNING){
                    	caseBreak = true;
                   	    break;
                     }
            	}
     
                if(actionContext.getStatus() == ActionContext.BREAK){
        		    actionContext.setStatus(ActionContext.RUNNING);
        		}  
        		
                if(caseBreak){
        			break;
        		}
        		
        		      		
            }
            
            //System.out.println("caseBreak=" + caseBreak + ",defaultObj=" + defaultObj + ",result=" + result);
        	if(!caseBreak && defaultObj != null){
            	for(Thing child : defaultObj.getChilds()){
                    Action action = world.getAction(child);
                    if(action != null){
                        result = action.run(actionContext, null, true);
                    }      
                     					
    				int sin = actionContext.getStatus();
      				if(sin != ActionContext.RUNNING){
       					break;
       				}        				
       			}
        		
        		//如果default里有break如何处理？应该是过滤。
        		if(actionContext.getStatus() == ActionContext.BREAK){
        		    actionContext.setStatus(ActionContext.RUNNING);
        		}
        	}
        	 
            return result;
        }finally{          
            //map.pop();
        }        
    }

}