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
package xworker.swt.xworker;

import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.design.Designer;

public class PagingToolbarCreator {
    public static Object create(ActionContext actionContext){
        World world = World.getInstance();
        Thing self = (Thing) actionContext.get("self");
        
		Thing compositeThing = world.getThing("xworker.swt.xworker.PagingToolbar/@pagingComposite");
		
		Composite composite = null;
		Designer.pushCreator(self);
		try{
			composite = (Composite) compositeThing.doAction("create", actionContext);
		}finally{
			Designer.popCreator();
		}
		Bindings bindings = actionContext.push(null);
		bindings.put("parent", composite);
		try{
		    for(Thing child : self.getAllChilds()){
		        child.doAction("create", actionContext);
		    }
		}finally{
		    actionContext.pop();
		}
		
		Designer.attachCreator(composite, self.getMetadata().getPath(), actionContext);
		return composite;        
	}
}