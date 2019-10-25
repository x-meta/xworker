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

public class TimerActions {
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
        	String varname = self.getStringBlankAsNull("varname");
        	if(varname != null){
        		scriptCode = varname + " = ";
        	}
        	if(self.getBoolean("executeOnce")){
        		scriptCode = scriptCode + "window.setTimeout(function(){";
        	}else{
        		scriptCode = scriptCode + "window.setInterval(function(){";
        	}
            String code = self.getString("code");
            if(self.getBoolean("useChildsCode")){
                code = "";
                for(Thing child : self.getChilds()){
                    if(code != ""){
                         code = code + "\n\n";
                    }
                    //log.info("child=" + child.getMetadata().getPath());
                    code = code + child.doAction("toJavaScriptCode", actionContext);
                }
            }
            
            if(code != null){
                for(String line : code.split("[\n]")){
                    scriptCode = scriptCode + "\n" + ident + "    " + line;
                }
            }
            scriptCode = scriptCode + "\n" + ident + "}, " + self.getString("interval") + ");";
            return scriptCode;
        }
    }

    public static String codeToJavaScriptCode(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	return self.getString("code");
    }
}