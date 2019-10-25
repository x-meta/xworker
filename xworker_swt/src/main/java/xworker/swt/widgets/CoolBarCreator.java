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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.design.Designer;
import xworker.swt.util.SwtUtils;

public class CoolBarCreator {
    public static Object create(ActionContext actionContext){
        Thing self = (Thing) actionContext.get("self");
		
		int style = SwtUtils.getInitStyle(self.getMetadata().getPath());
		//boolean vertical = false;
		String selfType = self.getString("type");
		if("HORIZONTAL".equals(selfType)){
			style |= SWT.HORIZONTAL;
		}else if("VERTICAL".equals(selfType)){
			style |= SWT.VERTICAL;
		}		
		
		if(self.getBoolean("BORDER"))
		    style |= SWT.BORDER;
		if(self.getBoolean("FLAT"))
		    style |= SWT.FLAT;
		    
		Composite parent = (Composite) actionContext.get("parent");
		CoolBar coolBar = new CoolBar(parent, style);
		
		//保存变量和创建子事物
		actionContext.getScope(0).put(self.getString("name"), coolBar);
		actionContext.peek().put("parent", coolBar);
		for(Thing child : self.getAllChilds()){
		    child.doAction("create", actionContext);
		}
		actionContext.peek().remove("parent");
		
		initCoolBar(coolBar);
		
		Designer.attach(coolBar, self.getMetadata().getPath(), actionContext);
		return coolBar;        
	}

    public static void initCoolBar(CoolBar coolBar){
    	for (CoolItem coolItem : coolBar.getItems()) {
		    Control control = coolItem.getControl();
		    if(control == null) continue;
		    Point size = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		    Point coolSize = coolItem.computeSize(size.x, size.y);
		    /*if (control instanceof ToolBar) {
			    ToolBar bar = (ToolBar)control;
			    if (bar.getItemCount() > 0) {
		            if (vertical) {
				        size.y = bar.getItem(0).getBounds().height;
			        } else {
				        size.x = bar.getItem(0).getWidth();
			        }
			    }
		    }*/
		
		    coolItem.setMinimumSize(size);
		    coolItem.setPreferredSize(coolSize);
		    coolItem.setSize(coolSize);
		}
    }
}