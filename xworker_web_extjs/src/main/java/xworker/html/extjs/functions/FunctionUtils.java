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

public class FunctionUtils {
	public static String getFunctionScript(Thing functionThing, ActionContext actionContext, String ident){
		String scriptCode = "";
        String code = functionThing.getString("code");
        if(functionThing.getBoolean("useChildsCode")){
            code = "";
            for(Thing child : functionThing.getChilds()){
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
        
        return scriptCode;
	}
}