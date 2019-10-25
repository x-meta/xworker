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
import org.xmeta.World;
import org.xmeta.util.UtilMap;

public class JasonDataActionCreator {
    public static Object run(ActionContext actionContext){
    	World world = World.getInstance();
        Thing jasonFormat = world.getThing("xworker.lang.text.JsonDataFormat");
        Thing self = (Thing) actionContext.get("self");
        
        Object targetObj = null;
        String method = self.getString("method");
        
        if("decodeFromCode".equals(method)){
            String code = self.getString("code");
            if(code != null){
                targetObj = jasonFormat.doAction("parse", actionContext, UtilMap.toParams(new Object[]{"jsonText", code}));
            }else{
                targetObj = null;
            }
        }else if("decodeFromVar".equals(method)){
            String code = (String) actionContext.get(self.getString("sourceVar"));
            if(code != null){
                targetObj = jasonFormat.doAction("parse", actionContext, UtilMap.toParams(new Object[]{"jsonText", code}));
            }else{
                targetObj = null;
            }
        }else if("encodeData".equals(method)){
            Object data = actionContext.get(self.getString("sourceVar"));
            targetObj = jasonFormat.doAction("format", actionContext, UtilMap.toParams(new Object[]{"data", data}));
        }
        
        String targetVar = self.getString("targetVar");
        if(targetVar != null && !"".equals(targetVar)){
            Bindings bindings = (Bindings) self.doAction("getVarScope", actionContext);
            if(bindings != null){
                bindings.put(targetVar, targetObj);
            }
        }
        
        //log.info("targetObj=" + targetObj);
        return targetObj;
    }

}