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
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.Action;
import javax.swing.JToolBar;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.JavaCreator;
import xworker.java.awt.AwtCreator;

public class JToolBarCreator {
	public static void createSeparator(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JToolBar parent = (JToolBar) actionContext.get("parent");
		
		Dimension size = AwtCreator.createDimension(self, "size", actionContext);
		if(size == null){
			parent.addSeparator();
		}else{
			parent.addSeparator(size);
		}
	}
	
	public static void createAction(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JToolBar parent = (JToolBar) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			Action obj = (Action) child.doAction("create", actionContext);
			if(obj != null){
				parent.add(obj);
			}
		}
	}
	
	public static JToolBar create(ActionContext actionContext){
		//变量
		Thing self = (Thing) actionContext.get("self");
		Container parent = (Container) actionContext.get("parent");
		
		//创建
		JToolBar comp = new JToolBar();
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
	
	public static void init(JToolBar comp, Thing thing, Container parent, ActionContext actionContext){
		JComponentCreator.init(comp, thing, parent, actionContext);
		
		Boolean borderPainted = JavaCreator.createBoolean(thing, "borderPainted");
		if(borderPainted != null){
			comp.setBorderPainted(borderPainted);
		}
		
		Boolean floatable = JavaCreator.createBoolean(thing, "floatable");
		if(floatable != null){
			comp.setFloatable(floatable);
		}
		
		Insets margin = AwtCreator.createInsets(thing, "margin", actionContext);
		if(margin != null){
			comp.setMargin(margin);
		}
		
		Integer orientation= null;
		String v = thing.getString("orientation");
		if("HORIZONTAL".equals(v)){
			orientation = JToolBar.HORIZONTAL;
		}else if("VERTICAL".equals(v)){
			orientation = JToolBar.VERTICAL;
		}
		if(orientation != null){
			comp.setOrientation(orientation);
		}
		
		Boolean rollover = JavaCreator.createBoolean(thing, "rollover");
		if(rollover != null){
			comp.setRollover(rollover);
		}
	}
}