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

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.event.MenuDragMouseListener;
import javax.swing.event.MenuKeyListener;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.JavaCreator;

public class JMenuItemCreator {
	public static void createMenuKeyListener(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JMenuItem parent = (JMenuItem) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			MenuKeyListener l = (MenuKeyListener) child.doAction("create", actionContext);
			if(l != null){
				parent.addMenuKeyListener(l);
			}
		}
	}
	
	public static void createMenuDragMouseListener(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JMenuItem parent = (JMenuItem) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			MenuDragMouseListener l = (MenuDragMouseListener) child.doAction("create", actionContext);
			if(l != null){
				parent.addMenuDragMouseListener(l);
			}
		}
	}
		
	public static JMenuItem create(ActionContext actionContext){
		//变量
		Thing self = (Thing) actionContext.get("self");
		
		//创建
		JMenuItem comp = new JMenuItem();
		
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
	
	public static void init(JMenuItem comp, Thing thing, Container parent, ActionContext actionContext){
		AbstractButtonCreator.init(comp, thing, parent, actionContext);
		
		Boolean armed = JavaCreator.createBoolean(thing, "armed");
		if(armed != null){
			comp.setArmed(armed);
		}
		
		KeyStroke accelerator = SwingCreator.createKeyStroke(thing, "accelerator", actionContext);
		if(accelerator != null){
			comp.setAccelerator(accelerator);
		}
	}
}