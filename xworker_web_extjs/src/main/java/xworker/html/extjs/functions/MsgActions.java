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
package xworker.html.extjs.functions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class MsgActions {
	public static String alert(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
    	
		return "Ext.Msg.alert(" + self.getString("title") + ", " + self.getString("message") + ");";        
	}
	
	public static String prompt(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
    	
        String ident = (String) actionContext.get("ident");
        if(ident == null){
            ident = "";
        }
        
        String scriptCode = "";
        String varref= self.getString("varref");
        if(varref != null && !"".equals(varref)){
            return varref;
        }else{
       		scriptCode = "Ext.Msg.prompt(" + self.getString("title") + ", " + self.getString("message") + ", function(btn, text){";
       		scriptCode = scriptCode + "\n" + "    if (btn == 'ok'){";
       		Thing okFunction = self.getThing("OkFunction@0");
       		if(okFunction != null){
       			String okScript = FunctionUtils.getFunctionScript(okFunction, actionContext, "    ");
       			if(okScript != null){
       				scriptCode = scriptCode + okScript;
       			}
       		}
       		scriptCode = scriptCode + "\n" + "    }else{";
       		Thing cancelFunction = self.getThing("CancelFunction@0");
       		if(cancelFunction != null){
       			String okScript = FunctionUtils.getFunctionScript(cancelFunction, actionContext, "    ");
       			if(okScript != null){
       				scriptCode = scriptCode + okScript;
       			}
       		}
       		scriptCode = scriptCode + "\n" + "    }\n}";
       		String scope = self.getStringBlankAsNull("scope");
       		if(scope != null){
       			scriptCode = scriptCode + ", " + scope;
       			String multiline = self.getStringBlankAsNull("multiline");
           		if(multiline != null){
           			scriptCode = scriptCode + ", " + multiline;
           			String value = self.getStringBlankAsNull("value");
               		if(value != null){
               			scriptCode = scriptCode + ", " + value;
               		}
           		}           		
       		}
       		
       		scriptCode = scriptCode + ");";
            
            return scriptCode;
        }
	}
	
	public static String confirm(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
    	
        String ident = (String) actionContext.get("ident");
        if(ident == null){
            ident = "";
        }
        
        String scriptCode = "";
        String varref= self.getString("varref");
        if(varref != null && !"".equals(varref)){
            return varref;
        }else{
        	Thing okFunction = self.getThing("OkFunction@0");
        	Thing cancelFunction = self.getThing("CancelFunction@0");
        	
        	if(okFunction != null || cancelFunction != null){
	       		scriptCode = "Ext.Msg.confirm(" + self.getString("title") + ", " + self.getString("message") + ", function(btn){";
	       		scriptCode = scriptCode + "\n" + "    if (btn == 'yes'){";
	       		
	       		if(okFunction != null){
	       			String okScript = FunctionUtils.getFunctionScript(okFunction, actionContext, "    ");
	       			if(okScript != null){
	       				scriptCode = scriptCode + okScript;
	       			}
	       		}
	       		scriptCode = scriptCode + "\n" + "    }else{";
	       		
	       		if(cancelFunction != null){
	       			String okScript = FunctionUtils.getFunctionScript(cancelFunction, actionContext, "    ");
	       			if(okScript != null){
	       				scriptCode = scriptCode + okScript;
	       			}
	       		}
	       		scriptCode = scriptCode + "\n" + "    }\n});";
        	}else{
        		scriptCode = "Ext.Msg.confirm(" + self.getString("title") + ", " + self.getString("message") + ");";
        	}        	
            
            return scriptCode;
        }
	}
	
}