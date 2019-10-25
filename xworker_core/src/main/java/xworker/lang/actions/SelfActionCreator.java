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

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.XMetaException;
import org.xmeta.util.UtilAction;

public class SelfActionCreator {
    public static Object getVarScope(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	return getScope(self, actionContext);
    	//throw new XMetaException("This method has bug, please use org.xmeta.util.UtilAction.getVarScope(Thing, ActionCotnext);");
    }

    public static Bindings getScope(Thing self, ActionContext actionContext){
    	return UtilAction.getVarScope(self, actionContext);
    	/*
    	Bindings binding = null;
        String varScope = self.getString("varScope");
        if(varScope == null || "".equals(varScope)){
        	binding = actionContext.getScope();
        }else  if("Global".equals(varScope)){
            binding = actionContext.getScope(0);    
        }else if("Local".equals(varScope)){
            binding = actionContext.getScope();
        }else{    
            try{
                int scopeIndex = Integer.parseInt(varScope);
                if(scopeIndex >= 0){
                    binding = actionContext.getScope(scopeIndex);
                }else{
                	binding = actionContext.getScope(actionContext.getScopesSize() + scopeIndex);
                }
            }catch(Exception e){
                binding = actionContext.getScope(varScope);
            }
        }
        
        return binding;
        */
    }
    
    public static void setVar(Thing selfActionThing, Object value, ActionContext actionContext){
    	throw new XMetaException("This method is deprecated, please use org.xmeta.util.UtilAction.putVarByActioScope(Thing action, String varName, Object value,  ActionCotnext actionContext);");
    }
    
    public static void setVar(ActionContext actionContext){
     	throw new XMetaException("This method is deprecated, please use org.xmeta.util.UtilAction.putVarByActioScope(Thing action, String varName, Object value,  ActionCotnext actionContext);");
    }

}