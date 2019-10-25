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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.design.Designer;
import xworker.swt.util.SwtUtils;

public class ToolBarCreator {
    public static Object create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
		
		int style = SwtUtils.getInitStyle(self.getMetadata().getPath());
		String selfType = self.getString("type");
		if("HORIZONTAL".equals(selfType)){
			style |= SWT.HORIZONTAL;
		}else if("VERTICAL".equals(selfType)){
			style |= SWT.VERTICAL;
		}
		
		if(self.getBoolean("FLAT"))
		    style |= SWT.FLAT;
		if(self.getBoolean("WRAP"))
		    style |= SWT.WRAP;
		if(self.getBoolean("BORDER"))
		    style |= SWT.BORDER;
		if(self.getBoolean("SHADOW_OUT"))
		    style |= SWT.SHADOW_OUT;
		if(self.getBoolean("RIGHT"))
		    style |= SWT.RIGHT;
		    
		Composite parent = (Composite) actionContext.get("parent");
		ToolBar bar = new ToolBar (parent, style);
		
		//保存变量和创建子事物
		actionContext.getScope(0).put(self.getString("name"), bar);
		actionContext.peek().put("parent", bar);
		for(Thing child : self.getAllChilds()){
		    child.doAction("create", actionContext);
		}
		//bar.pack();
		
		actionContext.peek().remove("parent");
		
		Designer.attach(bar, self.getMetadata().getPath(), actionContext);
		return bar;        
	}

    public static void initToolBar(ToolBar toolBar){
		for (ToolItem coolItem : toolBar.getItems()) {
		    Control ccontrol = coolItem.getControl();
		    if(ccontrol == null) continue;
		    Thing itemThing = Designer.getThing(coolItem);
		    int width = -1;
		    if(itemThing != null){
		    	width = itemThing.getInt("width", -1);
		    }
		    
		    if(width <= 0){
		    	width = ccontrol.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
		    }
		    coolItem.setWidth(width);
		}
    }
}