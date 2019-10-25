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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.design.Designer;
import xworker.swt.util.SwtUtils;

public class MenuCreator {
    public static Object create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
		
    	String selfStyle = self.getString("style");
		int style = SwtUtils.getInitStyle(self.getMetadata().getPath());
		if("BAR".equals(selfStyle)){
			style |= SWT.BAR;
		}else if("DROP_DOWN".equals(selfStyle)){
			style |= SWT.DROP_DOWN;
		}else if("POP_UP".equals(selfStyle)){
			style |= SWT.POP_UP;
		}
		
		if(self.getBoolean("NO_RADIO_GROUP"))
		    style |= SWT.NO_RADIO_GROUP;
		    
		String align = self.getString("align");
		if("LEFT_TO_RIGHT".equals(align)){
			style |= SWT.LEFT_TO_RIGHT;
		}else if("RIGHT_TO_LEFT".equals(align)){
			style |= SWT.RIGHT_TO_LEFT;
		}
		
		    
		Object parent = actionContext.get("parent");
		Menu menu = null;
		if(parent instanceof Menu){
			menu = new Menu((Menu) parent);
		}else if(parent instanceof MenuItem){
			menu = new Menu((MenuItem) parent);
		}else if(parent instanceof Decorations){
		    menu = new Menu((Decorations) parent, style);
		}else if(parent instanceof Control){
		    menu = new Menu((Control) parent);
		}
		
		if("false".equals(self.getString("visible"))){
		    menu.setVisible(false);
		}
		if("false".equals(self.getString("enabled"))){
		    menu.setEnabled(false);
		}
		
		if("BAR".equals(selfStyle) && parent instanceof Decorations){
		    ((Decorations) parent).setMenuBar(menu);
		}else if(parent instanceof Menu){
		}else if(parent instanceof MenuItem){
			((MenuItem) parent).setMenu(menu);
		}else if(parent instanceof Control){
		    ((Control) parent).setMenu(menu);
		}    
		
		//保存变量和创建子事物
		menu.setData(Designer.DATA_THING, self.getMetadata().getPath());
		actionContext.getScope(0).put(self.getString("name"), menu);
		actionContext.peek().put("parent", menu);
		for(Thing child : self.getAllChilds()){
		    child.doAction("create", actionContext);
		}
		actionContext.peek().remove("parent");
		
		return menu;        
	}
}