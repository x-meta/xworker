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
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;

public class SWITCHCreator {
    public static Object create(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");
        
        List<Thing> caseList = self.getChilds("Case");
        Thing defaultObj = self.getThing("Default@0");
        
        Object result = "success";
        try{
        	//Object v = UtilAction.runAsGroovy(self, "caseValue", actionContext, self.getMetadata().getPath()); 
        	Object v = OgnlUtil.getValue(self.getString("caseValue"), actionContext);
        	//System.out.println("case Value=" + v);
        	
        	//判断表达式
        	//boolean caseExecuted = false;
        	//boolean caseRunned = false;  //没有break时无条件执行下面的case
        	boolean caseBreak  = false;
            for(Thing caseObj : caseList){
        		//Object statementResult = UtilAction.runAsGroovy(caseObj, "condition", actionContext, caseObj.getMetadata().getPath()); 
        		Object statementResult = OgnlUtil.getValue(caseObj.getString("condition"), actionContext);
        		//System.out.println("statementResult=" + statementResult);
         		if((v == null && statementResult == null) || (v != null && v.equals(statementResult))){
        			//caseExecuted = true;
        			//caseRunned = true;
        			
        		    for(Thing child : caseObj.getAllChilds()){
                        result = child.doAction("create", actionContext, null, true);  
                     					
        				int sin = actionContext.getStatus();
        				if(sin != ActionContext.RUNNING){
        					caseBreak = true;
        					break;
        				}        				
        			}
        		}
     
                if(actionContext.getStatus() == ActionContext.BREAK){
        		    actionContext.setStatus(ActionContext.RUNNING);
        		}  
        		
                if(caseBreak){
        			break;
        		}
        		
        		      		
            }
            
            //System.out.println("caseBreak=" + caseBreak + ",defaultObj=" + defaultObj);
        	if(!caseBreak && defaultObj != null){
            	for(Thing child : defaultObj.getAllChilds()){
                    result = child.doAction("create", actionContext, null, true);      
                     					
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