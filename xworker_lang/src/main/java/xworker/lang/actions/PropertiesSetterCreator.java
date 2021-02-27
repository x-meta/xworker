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
package xworker.lang.actions;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;

public class PropertiesSetterCreator {
    public static void run(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");
        
        ActionContext dataSource = actionContext;
        String dataSourceStr = self.getString("dataSource");
        if(dataSourceStr != null && !"".equals(dataSourceStr)){
            dataSource = (ActionContext) OgnlUtil.getValue(dataSourceStr, actionContext);
        }
        
        if(dataSource == null){
            return;
        }
        
        try{
            Bindings bindings = (Bindings) actionContext.push(null);
            bindings.put("_newInstance", dataSource);
            for(Thing child : self.getChilds()){
                child.doAction("run", actionContext);
            }
        }finally{
            actionContext.pop();
        }
    }

}