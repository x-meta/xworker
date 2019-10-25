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

import javax.swing.DropMode;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.java.JavaCreator;
import xworker.java.awt.AwtCreator;

public class JListCreator {
	public static void createModel(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JList parent = (JList) actionContext.get("parent");
				
		for(Thing child : self.getChilds()){
			ListModel l = (ListModel) child.doAction("create", actionContext);
			if(l != null){
				parent.setModel(l);
			}
		}
	}
		
	public static void createListSelectionModel(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JList parent = (JList) actionContext.get("parent");
				
		for(Thing child : self.getChilds()){
			ListSelectionModel l = (ListSelectionModel) child.doAction("create", actionContext);
			if(l != null){
				parent.setSelectionModel(l);
			}
		}
	}
	
	public static void createDropMode(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JList parent = (JList) actionContext.get("parent");
				
		for(Thing child : self.getChilds()){
			DropMode l = (DropMode) child.doAction("create", actionContext);
			if(l != null){
				parent.setDropMode(l);
			}
		}
	}
	
	public static void createListCellRenderer(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JList parent = (JList) actionContext.get("parent");
				
		for(Thing child : self.getChilds()){
			ListCellRenderer l = (ListCellRenderer) child.doAction("create", actionContext);
			if(l != null){
				parent.setCellRenderer(l);
			}
		}
	}
	
	public static void createListSelectionListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JList parent = (JList) actionContext.get("parent");
				
		for(Thing child : self.getChilds()){
			ListSelectionListener l = (ListSelectionListener) child.doAction("create", actionContext);
			if(l != null){
				parent.addListSelectionListener(l);
			}
		}
	}
	
	public static JList create(ActionContext actionContext){
		//变量
		Thing self = (Thing) actionContext.get("self");
		Container parent = (Container) actionContext.get("parent");
		
		//创建
		JList comp = new JList();
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
	
	public static void init(JList comp, Thing thing, Container parent, ActionContext actionContext){
		JComponentCreator.init(comp, thing, parent, actionContext);
		
		Boolean dragEnabled = JavaCreator.createBoolean(thing, "dragEnabled");
		if(dragEnabled != null){
			comp.setDragEnabled(dragEnabled);
		}
		
		Integer fixedCellHeight = JavaCreator.createInteger(thing, "fixedCellHeight");
		if(fixedCellHeight != null){
			comp.setFixedCellHeight(fixedCellHeight);
		}
		
		Integer fixedCellWidth = JavaCreator.createInteger(thing, "fixedCellWidth");
		if(fixedCellWidth != null){
			comp.setFixedCellWidth(fixedCellWidth);
		}
		
		Integer layoutOrientation = null;
		String value = thing.getString("layoutOrientation");
		if("VERTICAL".equals(value)){
			layoutOrientation = JList.VERTICAL;
		}else if("HORIZONTAL_WRAP".equals(value)){
			layoutOrientation = JList.HORIZONTAL_WRAP;
		}else if("VERTICAL_WRAP".equals(value)){
			layoutOrientation = JList.VERTICAL_WRAP;
		}
		if(layoutOrientation != null){
			comp.setLayoutOrientation(layoutOrientation);
		}
		
		Color selectionBackground = AwtCreator.createColor(thing, "selectionBackground", actionContext);
		if(selectionBackground != null){
			comp.setSelectionBackground(selectionBackground);
		}
		
		Color selectionForeground = AwtCreator.createColor(thing, "selectionForeground", actionContext);
		if(selectionForeground != null){
			comp.setSelectionForeground(selectionForeground);
		}
		
		Integer selectionMode = null;
		value = thing.getString("selectionMode");
		if("SINGLE_SELECTION".equals(value)){
			selectionMode = ListSelectionModel.SINGLE_SELECTION;
		}else if("SINGLE_INTERVAL_SELECTION".equals(value)){
			selectionMode = ListSelectionModel.SINGLE_INTERVAL_SELECTION;
		}else if("MULTIPLE_INTERVAL_SELECTION".equals(value)){
			selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
		}
		if(selectionMode != null){
			comp.setSelectionMode(selectionMode);
		}
		
		Boolean valueIsAdjusting = JavaCreator.createBoolean(thing, "valueIsAdjusting");
		if(valueIsAdjusting != null){
			comp.setValueIsAdjusting(valueIsAdjusting);
		}
		
		Integer visibleRowCount = JavaCreator.createInteger(thing, "visibleRowCount");
		if(visibleRowCount != null){
			comp.setVisibleRowCount(visibleRowCount);
		}
	}
}