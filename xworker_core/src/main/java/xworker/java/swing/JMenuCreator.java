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

import java.awt.Container;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuListener;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.java.JavaCreator;

public class JMenuCreator {
	public static void createMenuListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JMenu parent = (JMenu) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			MenuListener l = (MenuListener) child.doAction("create", actionContext);
			if(l != null){
				parent.addMenuListener(l);
			}
		}
	}
	
	public static void createStringMenuItem(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JMenu parent = (JMenu) actionContext.get("parent");
		
		String text = JavaCreator.createText(self, "text", actionContext);
		if(text != null){
			parent.add(text);
		}
	}
	
	public static void createActionMenuItem(ActionContext actionContext){
		JMenu parent = (JMenu) actionContext.get("parent");
		
		Thing thing = World.getInstance().getThing("xworker.javax.swing.Action");
		Action item = (Action) thing.run("create", actionContext);
		if(item != null){
			parent.add(item);
		}
	}
	
	public static void createJMenuItem(ActionContext actionContext){
		JMenu parent = (JMenu) actionContext.get("parent");
		
		Thing thing = World.getInstance().getThing("xworker.javax.swing.JMenuItem");
		JMenuItem item = (JMenuItem) thing.run("create", actionContext);
		if(item != null){
			parent.add(item);
		}
	}
	
	public static JMenu create(ActionContext actionContext){
		//变量
		Thing self = (Thing) actionContext.get("self");
		
		//创建
		JMenu comp = new JMenu();
		
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
	
	public static void init(JMenu comp, Thing thing, Container parent, ActionContext actionContext){
		JMenuItemCreator.init(comp, thing, parent, actionContext);
		
		Integer delay = JavaCreator.createInteger(thing, "delay");
		if(delay != null){
			comp.setDelay(delay);
		}
		
		Boolean popupMenuVisible = JavaCreator.createBoolean(thing, "popupMenuVisible");
		if(popupMenuVisible != null){
			comp.setPopupMenuVisible(popupMenuVisible);
		}
	}
}