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

import javax.swing.DropMode;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.JavaCreator;
import xworker.java.awt.AwtCreator;

public class JTreeCreator {
	public static void createSelectionModel(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JTree parent = (JTree) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			TreeSelectionModel obj = (TreeSelectionModel) child.doAction("create", actionContext);
			if(obj != null){
				parent.setSelectionModel(obj);
				break;
			}
		}
	}
	
	public static void createModel(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JTree parent = (JTree) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			TreeModel obj = (TreeModel) child.doAction("create", actionContext);
			if(obj != null){
				parent.setModel(obj);
				break;
			}
		}
	}
	
	public static void createCellRenderer(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JTree parent = (JTree) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			TreeCellRenderer obj = (TreeCellRenderer) child.doAction("create", actionContext);
			if(obj != null){
				parent.setCellRenderer(obj);
				break;
			}
		}
	}
	
	public static void createTreeWillExpandListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JTree parent = (JTree) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			TreeWillExpandListener obj = (TreeWillExpandListener) child.doAction("create", actionContext);
			if(obj != null){
				parent.addTreeWillExpandListener(obj);
			}
		}
	}
	
	public static void createCellEditor(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JTree parent = (JTree) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			TreeCellEditor obj = (TreeCellEditor) child.doAction("create", actionContext);
			if(obj != null){
				parent.setCellEditor(obj);
				break;
			}
		}
	}
	
	public static void createTreeSelectionListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JTree parent = (JTree) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			TreeSelectionListener obj = (TreeSelectionListener) child.doAction("create", actionContext);
			if(obj != null){
				parent.addTreeSelectionListener(obj);
			}
		}
	}
	
	public static void createTreeExpansionListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JTree parent = (JTree) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			TreeExpansionListener obj = (TreeExpansionListener) child.doAction("create", actionContext);
			if(obj != null){
				parent.addTreeExpansionListener(obj);
			}
		}
	}
	
	public static JTree create(ActionContext actionContext){
		//变量
		Thing self = (Thing) actionContext.get("self");
		Container parent = (Container) actionContext.get("parent");
		
		//创建
		JTree comp = new JTree();
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
	
	public static void init(JTree comp, Thing thing, Container parent, ActionContext actionContext){
		JComponentCreator.init(comp, thing, parent, actionContext);
		
		Boolean dragEnabled = JavaCreator.createBoolean(thing, "dragEnabled");
		if(dragEnabled != null){
			comp.setDragEnabled(dragEnabled);
		}
		
		DropMode dropMode = AwtCreator.createDropMode(thing, "dropMode", actionContext);
		if(dropMode != null){
			comp.setDropMode(dropMode);
		}
		
		Boolean editable = JavaCreator.createBoolean(thing, "editable");
		if(editable != null){
			comp.setEditable(editable);
		}
		
		Boolean expandsSelectedPaths = JavaCreator.createBoolean(thing, "expandsSelectedPaths");
		if(expandsSelectedPaths != null){
			comp.setExpandsSelectedPaths(expandsSelectedPaths);
		}
		
		Boolean invokesStopCellEditing = JavaCreator.createBoolean(thing, "invokesStopCellEditing");
		if(invokesStopCellEditing != null){
			comp.setInvokesStopCellEditing(invokesStopCellEditing);
		}
		
		Boolean largeModel = JavaCreator.createBoolean(thing, "largeModel");
		if(largeModel != null){
			comp.setLargeModel(largeModel);
		}
		
		Boolean rootVisible = JavaCreator.createBoolean(thing, "rootVisible");
		if(rootVisible != null){
			comp.setRootVisible(rootVisible);
		}
		
		Integer rowHeight = JavaCreator.createInteger(thing, "rowHeight");
		if(rowHeight != null){
			comp.setRowHeight(rowHeight);
		}
		
		Boolean scrollsOnExpand = JavaCreator.createBoolean(thing, "scrollsOnExpand");
		if(scrollsOnExpand != null){
			comp.setScrollsOnExpand(scrollsOnExpand);
		}
		
		Boolean showsRootHandles = JavaCreator.createBoolean(thing, "showsRootHandles");
		if(showsRootHandles != null){
			comp.setShowsRootHandles(showsRootHandles);
		}
		
		Integer toggleClickCount = JavaCreator.createInteger(thing, "toggleClickCount");
		if(toggleClickCount != null){
			comp.setToggleClickCount(toggleClickCount);
		}
		
		Integer visibleRowCount = JavaCreator.createInteger(thing, "visibleRowCount");
		if(visibleRowCount != null){
			comp.setVisibleRowCount(visibleRowCount);
		}
	}
}