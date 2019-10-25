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

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;

public class IFCreator {
    public static Object create(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");

        //System.out.println("if condition = " + self.getString("condition"));        
        List<Thing> elseIfList = self.getChilds("ELSEIF");
        Thing elseObj = self.getThing("ELSE@0");
        
        Object result = "success";
        try{
        	List<Thing> statements = null;
        	//System.out.println(UtilAction.runAsGroovy(self, "condition", actionContext, self.getMetadata().getPath()));
        	//if(UtilAction.returnTrueFalse(UtilAction.runAsGroovy(self, "condition", actionContext, self.getMetadata().getPath()))){
        	if((Boolean) OgnlUtil.getValue(self.getString("condition"), actionContext)){
        	    //System.out.println("if is ok");
        		//If
        		statements = self.getAllChilds();
        		List<Thing> forRemoved = new ArrayList<Thing>();
        		for(Thing s : statements){
        			//过滤ElseIf和Else
        			if(s.isThingByName("ELSEIF") || s.isThingByName("ELSE")){
        				forRemoved.add(s);
        			}
        		}
        		
        		for(Thing r : forRemoved){
        			statements.remove(r);
        		}
        	}else{
        		//执行ElseIf
        		boolean isElseIf = false;
        		for(Thing elseIf : elseIfList){
        		    //if(UtilAction.returnTrueFalse(UtilAction.runAsGroovy(elseIf, "condition", actionContext, self.getMetadata().getPath()))){
        			//if(UtilJavaScript.runLogicScript(elseIf.getString("condition"), actionContext)){
        			//System.out.println("elseIf ok=" + OgnlUtil.getValue(self.getString("condition"), actionContext));
        		    if((Boolean) OgnlUtil.getValue(elseIf.getString("condition"), actionContext)){
        				isElseIf = true;
        				statements = elseIf.getAllChilds();
        			}
        		}
        		
        		if(!isElseIf && elseObj != null){
        			statements = elseObj.getAllChilds();
        		}
        	}
        	
        	if(statements != null){
        		for(Thing child : statements){
                    result = child.doAction("create", actionContext, null, true);      
                    
					if(actionContext.getStatus() != ActionContext.RUNNING){
						break;
					}
                }
        	}            
            
            return result;
        }finally{                        
        }        
    }

}