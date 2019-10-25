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
package xworker.lang;

import java.util.HashMap;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ClassBaseCreator {
    @SuppressWarnings("unchecked")
	public static void addObjectToContext(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
        String name = self.getString("xw_cl_varName");
        Object obj = actionContext.get("obj");
        //log.info(name + "=" + obj);
        if(name == null || "".equals(name) || obj == null){
            return;
        }
        
        if(self.getBoolean("xw_cl_global")){
            actionContext.getScope(0).put(name, obj);
        }
        
        String names = self.getString("xw_cl_namespace");
        if(names != null && !"".equals(names)){
            Map<String, Object> ns = null;
            for(String n : names.split("[.]")){
                if(ns == null){
                    ns = (Map<String, Object>) actionContext.getScope(0).get(n);
                    if(ns == null){
                        ns = new HashMap<String, Object>();
                        actionContext.getScope(0).put(n, ns);
                    }
                }else{
                    Map<String, Object> nss = (Map<String, Object>) ns.get(n);
                    if(nss == null){
                        nss = new HashMap<String, Object>();
                        ns.put(n, nss);
                    }
                    ns = nss;
                }
            }
            
            if(ns != null){
                ns.put(name, obj);
            }
        }
    }

}