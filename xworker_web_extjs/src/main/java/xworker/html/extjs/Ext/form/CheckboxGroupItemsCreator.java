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
package xworker.html.extjs.Ext.form;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

public class CheckboxGroupItemsCreator {
    public static Object toJavaScriptCode(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        String ident = (String) actionContext.get("ident");
        if(ident == null){
            ident = "";
        }
        
        String script = "";
        for(Thing item : self.getChilds()){
            String code = (String) item.doAction("toJavaScriptCode", actionContext, UtilMap.toMap(new Object[]{"ident", ident + "    "}));
            if(script != ""){
                script = script + ",\n" + ident + "    " + code + ")";
            }else{
                script = ident + "    " + code + ")";
            }
        }
        
        return "[\n" + script + "\n" + ident + "]";
    }

}