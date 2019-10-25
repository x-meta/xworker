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
package xworker.app.view.swt.app.workbentch;

import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class ViewCreator {
    public static Object createControl(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	World world = World.getInstance();
        
        //如果有引用，使用引用
        if(self.getBoolean("useRef")){
            Thing refThing = world.getThing(self.getString("refPath"));
            if(refThing != null){
                return refThing.doAction("createControl", actionContext);
            }
        }
        
        //只有第一个Control子节点生效
        Thing controlThing = self.getThing("Control@0");
        if(controlThing == null){
            return null;
        }
        
        Control control = null;
        for(Thing child : controlThing.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof Control){
                control = (Control) obj;
            }
        }
        
        return control;
    }

}