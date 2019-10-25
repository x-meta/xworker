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
package xworker.java.swing.table;

import java.awt.Container;
import java.beans.PropertyChangeListener;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;
import xworker.java.JavaCreator;

public class TableColumnCreator {
	
	public static void createHeaderRenderer(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		TableColumn parent = (TableColumn) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			TableCellRenderer obj = (TableCellRenderer) child.doAction("create", actionContext);
			if(obj != null){
				parent.setHeaderRenderer(obj);
				break;
			}
		}
	}
	
	public static void createCellRenderer(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		TableColumn parent = (TableColumn) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			TableCellRenderer obj = (TableCellRenderer) child.doAction("create", actionContext);
			if(obj != null){
				parent.setCellRenderer(obj);
				break;
			}
		}
	}
	
	public static void createCellEditor(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		TableColumn parent = (TableColumn) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			TableCellEditor obj = (TableCellEditor) child.doAction("create", actionContext);
			if(obj != null){
				parent.setCellEditor(obj);
				break;
			}
		}
	}
	
	public static void createPropertyChangeListeners(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		TableColumn parent = (TableColumn) actionContext.get("parent");
		
		for(Thing child : self.getChilds()){
			PropertyChangeListener obj = (PropertyChangeListener) child.doAction("create", actionContext);
			if(obj != null){
				parent.addPropertyChangeListener(obj);
			}
		}
	}
	
	public static TableColumn create(ActionContext actionContext) throws OgnlException{
		//变量
		Thing self = (Thing) actionContext.get("self");
				
		//创建
		TableColumn comp = new TableColumn();
		
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
	
	public static void init(TableColumn comp, Thing thing, Container parent, ActionContext actionContext) throws OgnlException{
		Object headerValue = OgnlUtil.getValue(thing, "headerValue", actionContext);
		if(headerValue != null){
			comp.setHeaderValue(headerValue);
		}
		
		Object identifier = OgnlUtil.getValue(thing, "identifier", actionContext);
		if(identifier != null){
			comp.setIdentifier(identifier);
		}
		
		Integer maxWidth = JavaCreator.createInteger(thing, "maxWidth");
		if(maxWidth != null){
			comp.setMaxWidth(maxWidth);
		}
		
		Integer minWidth = JavaCreator.createInteger(thing, "minWidth");
		if(minWidth != null){
			comp.setMinWidth(minWidth);
		}
		
		Integer modelIndex = JavaCreator.createInteger(thing, "modelIndex");
		if(modelIndex != null){
			comp.setModelIndex(modelIndex);
		}
		
		Integer preferredWidth = JavaCreator.createInteger(thing, "preferredWidth");
		if(preferredWidth != null){
			comp.setPreferredWidth(preferredWidth);
		}
		
		Boolean resizable = JavaCreator.createBoolean(thing, "resizable");
		if(resizable != null){
			comp.setResizable(resizable);
		}
		
		Integer width = JavaCreator.createInteger(thing, "width");
		if(width != null){
			comp.setWidth(width);
		}
	}
}