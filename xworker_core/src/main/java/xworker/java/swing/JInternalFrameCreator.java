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

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.beans.PropertyVetoException;

import javax.swing.Icon;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.event.InternalFrameListener;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.JavaCreator;
import xworker.java.awt.AwtCreator;

public class JInternalFrameCreator {
	public static void createInternalFrameListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JInternalFrame parent = (JInternalFrame) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			InternalFrameListener c = (InternalFrameListener) child.doAction("create", actionContext);
			if(c != null){
				parent.addInternalFrameListener(c);
			}
		}
	}
	
	public static void createLayeredPane(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JInternalFrame parent = (JInternalFrame) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			JLayeredPane c = (JLayeredPane) child.doAction("create", actionContext);
			if(c != null){
				parent.setLayeredPane(c);
				break;
			}
		}
	}
	
	public static void createJMenuBar(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JInternalFrame parent = (JInternalFrame) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			JMenuBar c = (JMenuBar) child.doAction("create", actionContext);
			if(c != null){
				parent.setJMenuBar(c);
				break;
			}
		}
	}
	
	public static void createGlassPane(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JInternalFrame parent = (JInternalFrame) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			Component c = (Component) child.doAction("create", actionContext);
			if(c != null){
				parent.setGlassPane(c);
				break;
			}
		}
	}
	
	public static void createContentPane(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JInternalFrame parent = (JInternalFrame) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			Container c = (Container) child.doAction("create", actionContext);
			if(c != null){
				parent.setContentPane(c);
				break;
			}
		}
	}
	
	public static JInternalFrame create(ActionContext actionContext){
		//变量
		Thing self = (Thing) actionContext.get("self");
		Container parent = (Container) actionContext.get("parent");
		
		//创建
		JInternalFrame comp = new JInternalFrame();
		if(parent != null){
			parent.add(comp);
		}
		
		//初始化
		init(comp, self, parent, actionContext);
		
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
	
	public static void init(JInternalFrame comp, Thing thing, Container parent, ActionContext actionContext){
		JComponentCreator.init(comp, thing, parent, actionContext);
		
		String title = JavaCreator.createText(thing, "title", actionContext);
		if(title != null){
			comp.setTitle(title);
		}
		
		Boolean closable = JavaCreator.createBoolean(thing, "closable");
		if(closable != null){
			comp.setClosable(closable);
		}
		
		Boolean closed = JavaCreator.createBoolean(thing, "closed");
		if(closed != null){
			try {
				comp.setClosed(closed);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}
		
		Cursor cursor = AwtCreator.createCursor(thing, "cursor", actionContext);
		if(cursor != null){
			comp.setCursor(cursor);
		}
		
		Integer defaultCloseOperation = null;
		String d = thing.getString("defaultCloseOperation");
		if("DO_NOTHING_ON_CLOSE".equals(d)){
			defaultCloseOperation = JInternalFrame.DO_NOTHING_ON_CLOSE;
		}else if("HIDE_ON_CLOSE".equals(d)){
			defaultCloseOperation = JInternalFrame.HIDE_ON_CLOSE;
		}else if("DISPOSE_ON_CLOSE".equals(d)){
			defaultCloseOperation = JInternalFrame.DISPOSE_ON_CLOSE;
		}
		if(defaultCloseOperation != null){
			comp.setDefaultCloseOperation(defaultCloseOperation);
		}
		
		Boolean focusCycleRoot = JavaCreator.createBoolean(thing, "focusCycleRoot");
		if(focusCycleRoot != null){
			comp.setFocusCycleRoot(focusCycleRoot);
		}
		
		Icon frameIcon = SwingCreator.createIcon(thing, "frameIcon", actionContext);
		if(frameIcon != null){
			comp.setFrameIcon(frameIcon);
		}
		
		Boolean setIcon = JavaCreator.createBoolean(thing, "setIcon");
		if(setIcon != null){
			try {
				comp.setIcon(setIcon);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}
		
		Boolean iconifiable = JavaCreator.createBoolean(thing, "iconifiable");
		if(iconifiable != null){
			comp.setIconifiable(iconifiable);
		}
		
		Integer layer = JavaCreator.createInteger(thing, "layer");
		if(layer != null){
			comp.setLayer(layer);
		}
		
		Boolean maximizable = JavaCreator.createBoolean(thing, "maximizable");
		if(maximizable != null){
			comp.setMaximizable(maximizable);
		}
		
		Boolean maximum = JavaCreator.createBoolean(thing, "maximum");
		if(maximum != null){
			try {
				comp.setMaximum(maximum);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}
		
		Boolean resizable = JavaCreator.createBoolean(thing, "resizable");
		if(resizable != null){
			comp.setResizable(resizable);
		}
		
		Boolean selected = JavaCreator.createBoolean(thing, "selected");
		if(selected != null){
			try {
				comp.setSelected(selected);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}
	}
}