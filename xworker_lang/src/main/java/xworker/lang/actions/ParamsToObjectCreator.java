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

public class ParamsToObjectCreator {
    public static Object run(ActionContext actionContext) throws OgnlException{
        Thing self = (Thing) actionContext.get("self");
        World world = World.getInstance();
        
        Object value = null;
        String params = (String) OgnlUtil.getValue(self.getString("paramsName"), actionContext);
        if(params != null && !"".equals(params)){
            Thing thing = world.getThing(self.getString("thingPath"));
            if(thing != null){
                Map<String, String> values = UtilString.getParams(params);
                Thing vthing = new Thing(thing.getMetadata().getPath());
                vthing.getAttributes().putAll(values);
                value = vthing.run("create", actionContext);
            }
        }
        
        return value;
    }

}