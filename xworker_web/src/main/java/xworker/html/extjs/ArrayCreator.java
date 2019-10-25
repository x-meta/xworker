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

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

public class ArrayCreator {
    public static Object toJavaScriptCode(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        String ident = (String) actionContext.get("ident");
        if(ident == null){
            ident = "";
        }
        
        String code = "";
        String selfCode = self.getString("code");
        if(selfCode == null || "".equals(selfCode)){
            for(Thing item : self.getChilds()){
                String script = null;
                String varref = item.getString("varref");
                if(varref != null && !"".equals(varref)){
                    script = varref;
                }else{
                    script = (String) item.doAction("toJavaScriptCode", actionContext, UtilMap.toMap(new Object[]{"ident", ident + "    "}));
                }
                if(script == null || script == ""){
                    continue;
                }
                if(item.getBoolean("haveTypeInCode")){
                    String type = (String) item.doAction("getExtType", actionContext);
                    script = "new " + type + "(" + script + ")";
                }
                if(code != ""){
                    code = code + ",\n" + ident + "    " + script;
                }else{
                    code = ident + "    " + script;
                }
            }
            
            return "[\n" + code + "\n" + ident + "]";
        }else{
            code = "";            
            for(String line : selfCode.split("[\n]")){
                if(code != ""){
                    code = code + "\n" + ident + "    " + line;
                }else{
                    code = ident + "    " + line;
                }
            }
            return "[\n" + code + "\n" + ident + "]";
        }
    }

}