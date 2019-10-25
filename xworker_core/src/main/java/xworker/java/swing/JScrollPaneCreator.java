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

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.border.Border;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.java.JavaCreator;

public class JScrollPaneCreator {
	
	public static void createColumnHeaderView(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JScrollPane parent = (JScrollPane) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			Component obj = (Component) child.doAction("create", actionContext);
			if(obj != null){
				parent.setColumnHeaderView(obj);
				break;
			}
		}
	}
	
	public static void createRowHeaderView(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JScrollPane parent = (JScrollPane) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			Component obj = (Component) child.doAction("create", actionContext);
			if(obj != null){
				parent.setRowHeaderView(obj);
				break;
			}
		}
	}
	
	public static void createViewportView(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JScrollPane parent = (JScrollPane) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			Component obj = (Component) child.doAction("create", actionContext);
			if(obj != null){
				parent.setViewportView(obj);
				break;
			}
		}
	}
	
	public static void createViewportBorder(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JScrollPane parent = (JScrollPane) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			Border obj = (Border) child.doAction("create", actionContext);
			if(obj != null){
				parent.setViewportBorder(obj);
				break;
			}
		}
	}
	
	public static void createViewport(ActionContext actionContext){
		JScrollPane parent = (JScrollPane) actionContext.get("parent");
		
		Thing thing = World.getInstance().getThing("xworker.javax.swing.JViewport");
		JViewport obj = (JViewport) thing.run("create", actionContext);
		if(obj != null){
			parent.setViewport(obj);
		}
	}
	
	public static void createVerticalScrollBar(ActionContext actionContext){
		JScrollPane parent = (JScrollPane) actionContext.get("parent");
		
		Thing thing = World.getInstance().getThing("xworker.javax.swing.JScrollBar");
		JScrollBar obj = (JScrollBar) thing.run("create", actionContext);
		if(obj != null){
			parent.setVerticalScrollBar(obj);
		}
	}
	
	public static void createRowHeader(ActionContext actionContext){
		JScrollPane parent = (JScrollPane) actionContext.get("parent");
		
		Thing thing = World.getInstance().getThing("xworker.javax.swing.JViewport");
		JViewport obj = (JViewport) thing.run("create", actionContext);
		if(obj != null){
			parent.setRowHeader(obj);
		}
	}
	
	public static void createHorizontalScrollBar(ActionContext actionContext){
		JScrollPane parent = (JScrollPane) actionContext.get("parent");
		
		Thing thing = World.getInstance().getThing("xworker.javax.swing.JScrollBar");
		JScrollBar obj = (JScrollBar) thing.run("create", actionContext);
		if(obj != null){
			parent.setHorizontalScrollBar(obj);
		}
	}
	
	public static void createColumnHeader(ActionContext actionContext){
		JScrollPane parent = (JScrollPane) actionContext.get("parent");
		
		Thing thing = World.getInstance().getThing("xworker.javax.swing.JViewport");
		JViewport obj = (JViewport) thing.run("create", actionContext);
		if(obj != null){
			parent.setColumnHeader(obj);
		}
	}
	
	public static JScrollPane create(ActionContext actionContext){
		//变量
		Thing self = (Thing) actionContext.get("self");
		Container parent = (Container) actionContext.get("parent");
		
		//创建
		JScrollPane comp = new JScrollPane();
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
	
	public static void init(JScrollPane comp, Thing thing, Container parent, ActionContext actionContext){
		JComponentCreator.init(comp, thing, parent, actionContext);
		
		Integer horizontalScrollBarPolicy = null;
		String v = thing.getString("horizontalScrollBarPolicy");
		if("HORIZONTAL_SCROLLBAR_AS_NEEDED".equals(v)){
			horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED;
		}else if("HORIZONTAL_SCROLLBAR_NEVER".equals(v)){
			horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;
		}else if("HORIZONTAL_SCROLLBAR_ALWAYS".equals(v)){
			horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS;
		}
		if(horizontalScrollBarPolicy != null){
			comp.setHorizontalScrollBarPolicy(horizontalScrollBarPolicy);
		}
		
		Integer verticalScrollBarPolicy = null;
		v = thing.getString("verticalScrollBarPolicy");
		if("VERTICAL_SCROLLBAR_AS_NEEDED".equals(v)){
			verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED;
		}else if("VERTICAL_SCROLLBAR_NEVER".equals(v)){
			verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_NEVER;
		}else if("VERTICAL_SCROLLBAR_ALWAY".equals(v)){
			verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;
		}
		if(verticalScrollBarPolicy != null){
			comp.setVerticalScrollBarPolicy(verticalScrollBarPolicy);
		}
		
		Boolean wheelScrollingEnabled = JavaCreator.createBoolean(thing, "wheelScrollingEnabled");
		if(wheelScrollingEnabled != null){
			comp.setWheelScrollingEnabled(wheelScrollingEnabled);
		}
	}
}