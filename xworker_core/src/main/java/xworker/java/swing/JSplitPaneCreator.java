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

import javax.swing.JSplitPane;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.JavaCreator;

public class JSplitPaneCreator {
	
	public static void createTopComponent(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JSplitPane parent = (JSplitPane) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			Component l = (Component) child.doAction("create", actionContext);
			if(l != null){
				parent.setTopComponent(l);
				break;
			}
		}
	}
	
	public static void createRightComponent(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JSplitPane parent = (JSplitPane) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			Component l = (Component) child.doAction("create", actionContext);
			if(l != null){
				parent.setRightComponent(l);
				break;
			}
		}
	}
	
	public static void createLeftComponent(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JSplitPane parent = (JSplitPane) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			Component l = (Component) child.doAction("create", actionContext);
			if(l != null){
				parent.setLeftComponent(l);
				break;
			}
		}
	}
	
	public static void createBottomComponent(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JSplitPane parent = (JSplitPane) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			Component l = (Component) child.doAction("create", actionContext);
			if(l != null){
				parent.setBottomComponent(l);
				break;
			}
		}
	}
	
	public static JSplitPane create(ActionContext actionContext){
		//变量
		Thing self = (Thing) actionContext.get("self");
		Container parent = (Container) actionContext.get("parent");
		
		//创建
		JSplitPane comp = new JSplitPane();
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
	
	public static void init(JSplitPane comp, Thing thing, Container parent, ActionContext actionContext){
		JComponentCreator.init(comp, thing, parent, actionContext);
	
		Boolean continuousLayout = JavaCreator.createBoolean(thing, "continuousLayout");
		if(continuousLayout != null){
			comp.setContinuousLayout(continuousLayout);
		}
		
		Double dividerLocationDouble = JavaCreator.createDouble(thing, "dividerLocationDouble");
		if(dividerLocationDouble != null){
			comp.setDividerLocation(dividerLocationDouble);
		}
		
		Integer dividerLocationInt = JavaCreator.createInteger(thing, "dividerLocationInt");
		if(dividerLocationInt != null){
			comp.setDividerLocation(dividerLocationInt);
		}
		
		Integer dividerSize = JavaCreator.createInteger(thing, "dividerSize");
		if(dividerSize != null){
			comp.setDividerSize(dividerSize);
		}
		
		Integer lastDividerLocation = JavaCreator.createInteger(thing, "lastDividerLocation");
		if(lastDividerLocation != null){
			comp.setLastDividerLocation(lastDividerLocation);
		}
		
		Boolean oneTouchExpandable = JavaCreator.createBoolean(thing, "oneTouchExpandable");
		if(oneTouchExpandable != null){
			comp.setOneTouchExpandable(oneTouchExpandable);
		}
		
		Integer orientation = null;
		String v = thing.getString("orientation");
		if("VERTICAL_SPLIT".equals(v)){
			orientation = JSplitPane.VERTICAL_SPLIT;
		}else if("HORIZONTAL_SPLIT".equals(v)){
			orientation = JSplitPane.HORIZONTAL_SPLIT;
		}
		if(orientation != null){
			comp.setOrientation(orientation);
		}
		
		Double resizeWeight = JavaCreator.createDouble(thing, "resizeWeight");
		if(resizeWeight != null){
			comp.setResizeWeight(resizeWeight);
		}
	}
}