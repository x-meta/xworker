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

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;

public class WHILECreator {
    public static Object create(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");
        
        Object result = "success";
        //while(UtilAction.returnTrueFalse(UtilAction.runAsGroovy(self, "condition", actionContext, self.getMetadata().getPath()))){            
        String condition = self.getString("condition");
        while((Boolean) OgnlUtil.getValue(condition, actionContext)){
            for(Thing child : self.getAllChilds()){
                result = child.doAction("create", actionContext, null, true);  
                 
        		int sint = actionContext.getStatus();
    	    	if(sint != ActionContext.RUNNING){
    				break;
    			}
            }
                
            if(actionContext.getStatus() == ActionContext.BREAK){
                actionContext.setStatus(ActionContext.RUNNING);
                break;
            }else if(actionContext.getStatus() == ActionContext.CONTINUE){
                actionContext.setStatus(ActionContext.RUNNING);
                continue;
            }else if(actionContext.getStatus() != ActionContext.RUNNING){
              	break;
            }
        }
            
        return result;      
    }

}