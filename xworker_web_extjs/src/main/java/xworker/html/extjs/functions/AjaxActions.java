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

public class AjaxActions {
	public static String request(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
    	
        return toJavaScriptCode(self, self.getString("url"), actionContext);
    }
	
	public static String dataOjectRequest(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
    	
		String url = "'do?sc=" + self.getString("dataObjectMethod") + "&dataObjectPath=" + self.getString("dataObjectPath") + "'";
		
		return toJavaScriptCode(self, url, actionContext);
	}
	
	private static String toJavaScriptCode(Thing self, String url, ActionContext actionContext){
		String ident = (String) actionContext.get("ident");
        if(ident == null){
            ident = "";
        }
        
        String scriptCode = "";
   		scriptCode = "Ext.Ajax.request({";
   		scriptCode = scriptCode + "\n    url: " + url;
   		scriptCode = getAttr(self, scriptCode, "method", "method");
   		scriptCode = getAttr(self, scriptCode, "timeout", "timeout");
   		scriptCode = getAttr(self, scriptCode, "scope", "scope");
   		scriptCode = getAttr(self, scriptCode, "params", "params");
   		scriptCode = getAttr(self, scriptCode, "headers", "headers");
   		
   		Thing callbackFunction = self.getThing("Callback@0");
   		if(callbackFunction != null){
   			String script = FunctionUtils.getFunctionScript(callbackFunction, actionContext, "    ");
   			if(script != null){
   				scriptCode = scriptCode + ",\n    callback: function(options, success, response){" + script + "\n    }";
   			}
   		}
   		Thing successFunction = self.getThing("Success@0");
   		if(successFunction != null){
   			String script = FunctionUtils.getFunctionScript(successFunction, actionContext, "    ");
   			if(script != null){
   				scriptCode = scriptCode + ",\n    success: function(response, options){" + script + "\n    }";
   			}
   		}
   		Thing failureFunction = self.getThing("Failure@0");
   		if(failureFunction != null){
   			String script = FunctionUtils.getFunctionScript(failureFunction, actionContext, "    ");
   			if(script != null){
   				scriptCode = scriptCode + ",\n    failure: function(response, options){" + script + "\n    }";
   			}
   		}
   		scriptCode = scriptCode + "\n" + "    \n});";
        
        return scriptCode;
	}
	
	private static String getAttr(Thing self, String scriptCode, String attributeName, String jsName){
		String value = self.getString(attributeName);
		if(value != null && !"".equals(value.trim())){
			String vs = null;
			for(String v : value.split("[\n]")){
				if(vs == null){
					vs = v;
				}else{
					vs = vs + "\n    " + v;
				}
			}
			return scriptCode + ",\n    " + jsName + ": " + vs;
		}
		return scriptCode;
	}
}