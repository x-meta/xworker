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
package xworker.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilString;

import xworker.swt.design.Designer;

public class GroupCreator {
    public static Object create(ActionContext actionContext){
        World world = World.getInstance();
        Thing self = (Thing) actionContext.get("self");
		
		Action styleAction = world.getAction("xworker.swt.widgets.Composite/@scripts/@getStyles");
		int style = (Integer) styleAction.run(actionContext);
		
		String selfStyle = self.getString("style");
		if("SHADOW_ETCHED_IN".equals(selfStyle)){
			style |= SWT.SHADOW_ETCHED_IN;
		}else if("SHADOW_ETCHED_OUT".equals(selfStyle)){
			style |= SWT.SHADOW_ETCHED_OUT;
		}else if("SHADOW_IN".equals(selfStyle)){
			style |= SWT.SHADOW_IN;
		}else if("SHADOW_OUT".equals(selfStyle)){
			style |= SWT.SHADOW_OUT;
		}else if("SHADOW_NON".equals(selfStyle)){
			style |= SWT.SHADOW_NONE;
		}
				
		if(self.getBoolean("BORDER"))
		    style |= SWT.BORDER;
		        
		Composite parent = (Composite) actionContext.get("parent");
		Group group = new Group(parent, style);
		    
		//父类的初始化方法
		Bindings bindings = actionContext.push(null);
		bindings.put("control", group);
		bindings.put("self", self);
		try{
		    Action initAction = world.getAction("xworker.swt.widgets.Control/@actions/@init");
		    initAction.run(actionContext);
		}finally{
		    actionContext.pop();
		}
		
		if(self.getString("text") != null)
		    group.setText(UtilString.getString(self.getString("text"), actionContext));
		
		//保存变量和创建子事物
		actionContext.getScope(0).put(self.getString("name"), group);
		actionContext.peek().put("parent", group);
		for(Thing child : self.getAllChilds()){
		    child.doAction("create", actionContext);
		}
		actionContext.peek().remove("parent");
		
		Designer.attach(group, self.getMetadata().getPath(), actionContext);
		return group;       
	}

}