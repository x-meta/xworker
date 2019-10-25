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

public class FunctionBlockCodeCreator {
    public static Object toJavaScriptCode(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        String code = "";
        String precode = self.getString("precode");
        if(precode != null && !"".equals(precode)){
            code = code + precode;
        }
        if(!self.getBoolean("useCode")){
            code = "";
            for(Thing child : self.getChilds()){
                if(code != ""){
                     code = code + "\n";
                }
                code = code + (String) child.doAction("toJavaScriptCode", actionContext, UtilMap.toMap(new Object[]{"ident", actionContext.get("ident") + "    "}));
            }
        }
        
        String aftercode = self.getString("aftercode");
        if(aftercode != null && !"".equals(aftercode)){
            if(code != ""){
                 code = code + "\n";
            }
            code = code + aftercode;
        }
        
        return code;
    }

}