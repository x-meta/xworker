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

public class ExtContainerCreator {
    public static Object toJavaScriptCode(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        return self.doAction("getChildJavaScriptCode", actionContext, UtilMap.toMap(new Object[]{"defaultWidget", self.getString("defaultWidget")}));
    }

    public static Object getChildJavaScriptCode(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        String ident = (String) actionContext.get("ident");
        if(ident == null){
            ident = "";
        }
        
        String code = "";
        Thing child = self.getThing("@0");
        if(child == null){
            return "new " + actionContext.get("defaultWidget") + "()";
        }else{    
        	String varref = child.getString("varref");
            if(varref != null && !"".equals(varref)){
                return varref;
            }else{
                code = (String) child.doAction("toJavaScriptCode", actionContext, UtilMap.toMap(new Object[]{"ident", ident}));
                if(code != null && code.startsWith("{")){
                    Object thingName = child.doAction("getExtType", actionContext);
                    code = "new " + thingName + "(" + code;
                    return code + ")";    	
                }else{
                    return code;
                }
            }
        }
    }

}