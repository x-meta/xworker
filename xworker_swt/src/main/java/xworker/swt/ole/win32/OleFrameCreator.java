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
package xworker.swt.ole.win32;

import org.eclipse.swt.SWT;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

public class OleFrameCreator {
    public static Object create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
    	Composite parent = (Composite) actionContext.get("parent");
    	OleFrame frame = new OleFrame(parent, self.getBoolean("BORDER") ?  SWT.BORDER : SWT.NONE);
		
		//父类的初始化方法
		Bindings bindings = actionContext.push(null);
		bindings.put("control", frame);
		bindings.put("self", self);
		try{
		    Action initAction = World.getInstance().getAction("xworker.swt.widgets.Control/@actions/@init");
		    initAction.run(actionContext);
		}finally{
		    actionContext.pop();
		}
		
		actionContext.getScope(0).put(self.getString("name"), frame);
		actionContext.peek().put("parent", frame);
		for(Thing child : self.getAllChilds()){
		    child.doAction("create", actionContext);
		}
		actionContext.peek().remove("parent");
		
		return frame;        
	}

}