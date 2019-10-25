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
package xworker.html.extjs.Ext;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

public class TemplateCreator {
    public static Object toJavaScriptCode(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
    	String varref = self.getString("varref");
        if(varref != null && !"".equals(varref)){
            return varref;
        }else{
            String scriptCode = (String) self.doAction("toCode", actionContext);
            
            if(scriptCode != ""){
                return "new Ext.Template(\n" + scriptCode + "\n" + actionContext.get("ident") + ")";
            }else{
                return null;
            }
        }
    }

    @SuppressWarnings("unchecked")
	public static Object toCode(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        String ident = (String) actionContext.get("ident");
        if(ident == null){
            ident = "";
        }
        String scriptCode = "";
        for(Thing html : (List<Thing>) self.get("html@")){
        	String code = html.getString("code");
            if(code != null && !"".equals(code)){
                scriptCode = scriptCode + ",\n" + ident + "    " + code;
            }
        }
        Thing config = self.getThing("Config@0");
        if(config != null){
            String configStr = (String) config.doAction("toJavaScriptCode", actionContext, UtilMap.toMap(new Object[]{"ident", ident + "    "}));
            if(configStr != null){
                if(scriptCode != ""){
                    scriptCode = scriptCode + ",\n" + ident + "    " + configStr;
                }
            }
        }
        if(scriptCode != ""){
            scriptCode = scriptCode.substring(2, scriptCode.length());
        }
        
        return scriptCode;
    }

}