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
package xworker.html.extjs.Ext.ux.grid;

import java.util.List;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class RowExpanderCreator {
    @SuppressWarnings("unchecked")
	public static Object toJavaScriptCode(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	World world = World.getInstance();
        Action extThingAction = world.getAction("xworker.html.extjs.ExtThing/@actions/@toJavaScriptCode");
        String code = (String) extThingAction.run(actionContext);
        
        if(actionContext.get("extGlobalVars") != null){
            code = "    var " + self.getString("name") + " = new Ext.ux.grid.RowExpander(" + code + ");";
            ((List<String>) actionContext.get("extGlobalVars")).add(code);
            
            return null;
        }else{
            return code;
        }
    }

}