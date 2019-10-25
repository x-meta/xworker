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
import java.awt.Container;

import javax.swing.JColorChooser;
import javax.swing.colorchooser.ColorSelectionModel;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.JavaCreator;
import xworker.java.awt.AwtCreator;

public class JColorChooserCreator {
	public static void createColorSelectionModel(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JColorChooser parent = (JColorChooser) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			ColorSelectionModel model = (ColorSelectionModel) child.doAction("create", actionContext);
			if(model != null){
				parent.setSelectionModel(model);
				break;
			}
		}
	}
	
	public static JColorChooser create(ActionContext actionContext){
		//变量
		Thing self = (Thing) actionContext.get("self");
		Container parent = (Container) actionContext.get("parent");
		
		//创建
		JColorChooser comp = new JColorChooser();
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
	
	public static void init(JColorChooser comp, Thing thing, Container parent, ActionContext actionContext){
		JComponentCreator.init(comp, thing, parent, actionContext);
		
		Color color = AwtCreator.createColor(thing, "color", actionContext);
		if(color != null){
			comp.setColor(color);
		}
		
		Boolean dragEnabled = JavaCreator.createBoolean(thing, "dragEnabled");
		if(dragEnabled != null){
			comp.setDragEnabled(dragEnabled);
		}
	}
}