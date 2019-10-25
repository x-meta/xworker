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

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.SingleSelectionModel;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.java.JavaCreator;

public class JMenuBarCreator {
	public static void createSelectionModel(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JMenuBar parent = (JMenuBar) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			SingleSelectionModel model = (SingleSelectionModel) child.doAction("create", actionContext);
			if(model != null){
				parent.setSelectionModel(model);
			}
		}
	}
	
	public static void createHelpMenu(ActionContext actionContext){
		JMenuBar parent = (JMenuBar) actionContext.get("parent");
		
		Thing thing = World.getInstance().getThing("xworker.javax.swing.JMenu");
		JMenu item = (JMenu) thing.run("create", actionContext);
		if(item != null){
			parent.setHelpMenu(item);
		}
	}
	
	public static void createJMenu(ActionContext actionContext){
		JMenuBar parent = (JMenuBar) actionContext.get("parent");
		
		Thing thing = World.getInstance().getThing("xworker.javax.swing.JMenu");
		JMenu item = (JMenu) thing.run("create", actionContext);
		if(item != null){
			parent.add(item);
		}
	}
	
	public static JMenuBar create(ActionContext actionContext){
		//变量
		Thing self = (Thing) actionContext.get("self");
		
		//创建
		JMenuBar comp = new JMenuBar();
		
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
	
	public static void init(JMenuBar comp, Thing thing, Container parent, ActionContext actionContext){
		JComponentCreator.init(comp, thing, parent, actionContext);
		
		Boolean borderPainted = JavaCreator.createBoolean(thing, "borderPainted");
		if(borderPainted != null){
			comp.setBorderPainted(borderPainted);
		}
	}
}