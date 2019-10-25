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
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.java.JavaCreator;
import xworker.java.awt.AwtCreator;

public class JTableCreator {
	public static void createModel(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JTable parent = (JTable) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			TableModel model = (TableModel) child.doAction("create", actionContext);
			if(model != null){
				parent.setModel(model);
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void createRowSorter(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JTable parent = (JTable) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			RowSorter model = (RowSorter) child.doAction("create", actionContext);
			if(model != null){
				parent.setRowSorter(model);
			}
		}
	}
	
	public static void createColumnModel(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		JTable parent = (JTable) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			TableColumnModel model = (TableColumnModel) child.doAction("create", actionContext);
			if(model != null){
				parent.setColumnModel(model);
			}
		}
	}
	
	public static void createColumn(ActionContext actionContext){
		JTable parent = (JTable) actionContext.get("parent");
		
		Thing thing = World.getInstance().getThing("xworker.javax.swing.table.TableColumn");
		TableColumn item = (TableColumn) thing.run("create", actionContext);
		if(item != null){
			parent.addColumn(item);
		}
	}
	
	public static JTable create(ActionContext actionContext){
		//变量
		Thing self = (Thing) actionContext.get("self");
		Container parent = (Container) actionContext.get("parent");
		
		//创建
		JTable comp = new JTable();
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
	
	public static void init(JTable comp, Thing thing, Container parent, ActionContext actionContext){
		JComponentCreator.init(comp, thing, parent, actionContext);
		
		Boolean autoCreateColumnsFromModel = JavaCreator.createBoolean(thing, "autoCreateColumnsFromModel");
		if(autoCreateColumnsFromModel != null){
			comp.setAutoCreateColumnsFromModel(autoCreateColumnsFromModel);
		}
		
		Boolean autoCreateRowSorter = JavaCreator.createBoolean(thing, "autoCreateRowSorter");
		if(autoCreateRowSorter != null){
			comp.setAutoCreateRowSorter(autoCreateRowSorter);
		}
		
		Integer autoResizeMode = null;
		String v = thing.getString("autoResizeMode");
		if("AUTO_RESIZE_OFF".equals(v)){
			autoResizeMode = JTable.AUTO_RESIZE_OFF;
		}else if("AUTO_RESIZE_NEXT_COLUMN".equals(v)){
			autoResizeMode = JTable.AUTO_RESIZE_NEXT_COLUMN;
		}else if("AUTO_RESIZE_SUBSEQUENT_COLUMNS".equals(v)){
			autoResizeMode = JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS;
		}else if("AUTO_RESIZE_LAST_COLUMN".equals(v)){
			autoResizeMode = JTable.AUTO_RESIZE_LAST_COLUMN;
		}else if("AUTO_RESIZE_ALL_COLUMNS".equals(v)){
			autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS;
		}
		if(autoResizeMode != null){
			comp.setAutoResizeMode(autoResizeMode);
		}
		
		Boolean cellSelectionEnabled = JavaCreator.createBoolean(thing, "cellSelectionEnabled");
		if(cellSelectionEnabled != null){
			comp.setCellSelectionEnabled(cellSelectionEnabled);
		}
		
		Boolean columnSelectionAllowed = JavaCreator.createBoolean(thing, "columnSelectionAllowed");
		if(columnSelectionAllowed != null){
			comp.setColumnSelectionAllowed(columnSelectionAllowed);
		}
		
		Boolean dragEnabled = JavaCreator.createBoolean(thing, "dragEnabled");
		if(dragEnabled != null){
			comp.setDragEnabled(dragEnabled);
		}
		
		DropMode dropMode = AwtCreator.createDropMode(thing, "dropMode", actionContext);
		if(dropMode != null){
			comp.setDropMode(dropMode);
		}
		
		Boolean fillsViewportHeight = JavaCreator.createBoolean(thing, "fillsViewportHeight");
		if(fillsViewportHeight != null){
			comp.setFillsViewportHeight(fillsViewportHeight);
		}
		
		Color gridColor = AwtCreator.createColor(thing, "gridColor", actionContext);
		if(gridColor != null){
			comp.setGridColor(gridColor);
		}
		
		Integer rowHeight = JavaCreator.createInteger(thing, "rowHeight");
		if(rowHeight != null){
			comp.setRowHeight(rowHeight);
		}
		
		Integer rowMargin = JavaCreator.createInteger(thing, "rowMargin");
		if(rowMargin != null){
			comp.setRowMargin(rowMargin);
		}
		
		Boolean rowSelectionAllowed = JavaCreator.createBoolean(thing, "rowSelectionAllowed");
		if(rowSelectionAllowed != null){
			comp.setRowSelectionAllowed(rowSelectionAllowed);
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
		v = thing.getString("selectionMode");
		if("MULTIPLE_INTERVAL_SELECTION".equals(v)){
			selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
		}else if("SINGLE_INTERVAL_SELECTION".equals(v)){
			selectionMode = ListSelectionModel.SINGLE_INTERVAL_SELECTION;
		}else if("SINGLE_SELECTION".equals(v)){
			selectionMode = ListSelectionModel.SINGLE_SELECTION;
		}
		if(selectionMode != null){
			comp.setSelectionMode(selectionMode);
		}
		
		Boolean showGrid = JavaCreator.createBoolean(thing, "showGrid");
		if(showGrid != null){
			comp.setShowGrid(showGrid);
		}
		
		Boolean showHorizontalLines = JavaCreator.createBoolean(thing, "showHorizontalLines");
		if(showHorizontalLines != null){
			comp.setShowHorizontalLines(showHorizontalLines);
		}
		
		Boolean showVerticalLines = JavaCreator.createBoolean(thing, "showVerticalLines");
		if(showVerticalLines != null){
			comp.setShowVerticalLines(showVerticalLines);
		}
		
		Boolean surrendersFocusOnKeystroke = JavaCreator.createBoolean(thing, "surrendersFocusOnKeystroke");
		if(surrendersFocusOnKeystroke != null){
			comp.setSurrendersFocusOnKeystroke(surrendersFocusOnKeystroke);
		}
		
		Boolean updateSelectionOnSort = JavaCreator.createBoolean(thing, "updateSelectionOnSort");
		if(updateSelectionOnSort != null){
			comp.setUpdateSelectionOnSort(updateSelectionOnSort);
		}
	}
}