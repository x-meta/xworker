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
package xworker.html.extjs.Ext.data;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

public class DataReaderFieldsCreator {
    @SuppressWarnings("unchecked")
	public static Object toJavaScriptCode(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	String code = self.getString("code");
        if(code != null && !"".equals(code)){
            return code;
        }
        
        String ident = (String) actionContext.get("ident");
        code = "[";
        List<Thing> childs = (List<Thing>) self.get("Field@");
        for(int i=0;i<childs.size(); i++){
            Thing child = childs.get(i);
            if(code != "["){
                code = code + ",";
            }
            code = code + "\n    " + ident + child.doAction("toJavaScriptCode", actionContext, UtilMap.toMap(new Object[]{"ident", ident + "    "}));
        }
        return code + "\n" + ident + "]";
    }

}