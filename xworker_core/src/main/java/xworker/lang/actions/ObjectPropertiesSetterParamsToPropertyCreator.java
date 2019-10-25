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

import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilString;

import ognl.OgnlException;

public class ObjectPropertiesSetterParamsToPropertyCreator {
    public static void run(ActionContext actionContext) throws OgnlException{
        Thing self = (Thing) actionContext.get("self");
        World world = World.getInstance();
        
        //log.info("ParamsToProperty, " + self.propertyName);
        Object value = null;
        String params = (String) OgnlUtil.getValue(self.getString("paramsName"), actionContext);
        if(params != null && !"".equals(params)){
            Thing thing = world.getThing(self.getString("thingPath"));
            //log.info("thing=" + thing);
            if(thing != null){
                Map<String, String> values = UtilString.getParams(params);
                //log.info("values=" + values);
                Thing vthing = new Thing(thing.getMetadata().getPath());
                vthing.getAttributes().putAll(values);        
                value = vthing.doAction("create", actionContext);
            }
        }
        
        //log.info("ParamsToProperty, " + self.propertyName + ",params=" + params + ",thingPath=" + self.thingPath + ",value=" + value);
        if(value != null || self.getBoolean("ignoreNull") == false){
        	//_object是传入的变量
        	OgnlUtil.setValue(self.getString("propertyName"), actionContext.get("_object"), value);
        }
    }

}