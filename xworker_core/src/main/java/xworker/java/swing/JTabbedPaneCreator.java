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
package xworker.java.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;

import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeListener;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import xworker.java.JavaCreator;
import xworker.java.awt.AwtCreator;

public class JTabbedPaneCreator {
	public static void createTabItem(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JTabbedPane parent = (JTabbedPane) actionContext.get("parent");
		
		Thing thing = self.getThing("Component@0");
		if(thing != null){
			try{
				Bindings bindings = actionContext.push();
				bindings.put("parent", null);
				
				for(Thing child : thing.getChilds()){
					Component c = (Component) child.doAction("create", actionContext);
					if(c != null){
						int index = parent.getTabCount();
						parent.setComponentAt(index, c);
						
						Color background = AwtCreator.createColor(thing, "background", actionContext);
						if(background != null){
							parent.setBackgroundAt(index, background);
						}
						
						Icon disabledIcon = SwingCreator.createIcon(thing, "disabledIcon", actionContext);
						if(disabledIcon != null){
							parent.setDisabledIconAt(index, disabledIcon);
						}
						
						Integer displayedMnemonicIndex = JavaCreator.createInteger(thing, "displayedMnemonicIndex");
						if(displayedMnemonicIndex != null){
							parent.setDisplayedMnemonicIndexAt(index, displayedMnemonicIndex);
						}
						
						Boolean enabled = JavaCreator.createBoolean(thing, "enabled");
						if(enabled != null){
							parent.setEnabledAt(index, enabled);
						}
						
						Color foreground = AwtCreator.createColor(thing, "foreground", actionContext);
						if(foreground != null){
							parent.setForeground(foreground);
						}
						
						Icon icon = SwingCreator.createIcon(thing, "icon", actionContext);
						if(icon != null){
							parent.setIconAt(index, icon);
						}
						
						Integer mnemonic = AwtCreator.createMnemonic(thing, "mnemonic");
						if(mnemonic != null){
							parent.setMnemonicAt(index, mnemonic);
						}
						
						Boolean selected = JavaCreator.createBoolean(thing, "selected");
						if(selected != null && selected){
							parent.setSelectedIndex(index);
						}
						
						String title = JavaCreator.createText(thing, "title", actionContext);
						if(title != null){
							parent.setTitleAt(index, title);
						}
						
						String toolTipText = JavaCreator.createText(thing, "toolTipText", actionContext);
						if(toolTipText != null){
							parent.setToolTipTextAt(index, toolTipText);
						}
						break;
					}
				}
			}finally{
				actionContext.pop();
			}
		}
		
	}
	
	public static void createChangeListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JTabbedPane parent = (JTabbedPane) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			ChangeListener l = (ChangeListener) child.doAction("create", actionContext);
			if(l != null){
				parent.addChangeListener(l);
			}
		}
	}
	
	public static JTabbedPane create(ActionContext actionContext){
		//变量
		Thing self = (Thing) actionContext.get("self");
		Container parent = (Container) actionContext.get("parent");
		
		//创建
		JTabbedPane comp = new JTabbedPane();
		if(parent != null){
			parent.add(comp);
		}
		
		//初始化
		init(comp, self, null, actionContext);
		
		//创建子节点
		try{
			actionContext.push().put("parent", comp);
			for(Thing child : self.getChilds()){
				child.doAction("create", actionContext);
			}
		}finally{
			actionContext.pop();
		}
		
		//放置和返回变量
		actionContext.getScope(0).put(self.getMetadata().getName(), comp);
		return comp;
	}
	
	public static void init(JTabbedPane comp, Thing thing, Container parent, ActionContext actionContext){
		JComponentCreator.init(comp, thing, parent, actionContext);
		
		Integer tabLayoutPolicy = null;
		String v = thing.getString("tabLayoutPolicy");
		if("WRAP_TAB_LAYOUT".equals(v)){
			tabLayoutPolicy = JTabbedPane.WRAP_TAB_LAYOUT;
		}else if("WRAP_TAB_LAYOUT".equals(v)){
			tabLayoutPolicy = JTabbedPane.WRAP_TAB_LAYOUT;
		}
		if(tabLayoutPolicy != null){
			comp.setTabLayoutPolicy(tabLayoutPolicy);
		}
		
		Integer tabPlacement = JavaCreator.createInteger(thing, "tabPlacement");
		if(tabPlacement != null){
			comp.setTabPlacement(tabPlacement);
		}
	}
}