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
package xworker.html.extjs;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import freemarker.template.TemplateException;
import xworker.html.extjs.functions.FunctionUtils;
import xworker.util.UtilTemplate;

public class FunctionCreator {
	public static String subCode(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		int identCount = self.getInt("identCount", 1);
		String ident = "";
		for(int i=0; i<identCount ;i++){
			ident = ident + "    ";
		}
		
		return FunctionUtils.getFunctionScript(self, actionContext, ident);
	}
	
    public static Object toJavaScriptCode(ActionContext actionContext){
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
        	String params = self.getString("params");
            scriptCode = "function(" + (params == null ? "" : params) + "){";
            String code = self.getString("code");
            if(self.getBoolean("useChildsCode")){
                code = "";
                for(Thing child : self.getChilds()){
                    if(code != ""){
                         code = code + "\n\n";
                    }
                    //log.info("child=" + child.getMetadata().getPath());
                    if(child.isThingByName("JavaScriptObject")){
                    	String type = (String) child.doAction("getJavaScriptObjectType", actionContext);
                    	code = code + "\nvar " + child.getString("varname") + " = new " + type + "(" + child.doAction("toJavaScriptCode", actionContext) + ");";
                    }else{                    	
                    	code = code + child.doAction("toJavaScriptCode", actionContext);
                    }
                }
            }
            
            if(code != null){
                for(String line : code.split("[\n]")){
                    scriptCode = scriptCode + "\n" + ident + "    " + line;
                }
            }
            scriptCode = scriptCode + "\n" + ident + "}";
            return scriptCode;
        }
    }

    public static String codeToJavaScriptCode(ActionContext actionContext) throws IOException, TemplateException{
    	Thing self = (Thing) actionContext.get("self");
    	
    	String code = self.getString("code");
    	if(self.getChilds().size() > 0){
    		Map<String, String> subCodes = new HashMap<String, String>();
    		for(Thing child : self.getChilds()){
    			subCodes.put(child.getString("name"), (String) child.doAction("toJavaScriptCode", actionContext));
    		}
    		
    		return UtilTemplate.processThingAttributeToString(self, "code", subCodes);
    	}else{
    		return code;
    	}
    }
}