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
package xworker.swt.custom;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionParams;
import org.xmeta.util.UtilMap;

import xworker.dataObject.DataObject;
import xworker.swt.custom.tableEditors.ItemEditorUtils;
import xworker.swt.events.SwtListener;
import xworker.swt.util.ResourceManager;
import xworker.swt.util.UtilEvent;

/**
 * 现已作废，参看TableCursorEditor.
 * @author zyx
 *
 */
public class TableCursorEditorCreator {
    public static Object create(ActionContext actionContext){
    	World world = World.getInstance();
    	Thing self = (Thing) actionContext.get("self");
    	
		int style = SWT.NONE;
		if(self.getBoolean("BORDER")){
		    style = style | SWT.BORDER;
		}
		
		Table parent = (Table) actionContext.get("parent");
		TableCursor cursor = new TableCursor(parent, style);
		parent.setData("tableCursor", cursor);
		
		//背景颜色
		Color background = (Color) ResourceManager.createResource(self.getString("background"), 
		        "xworker.swt.graphics.Color", "rgb", actionContext);
		if(background != null){
		    cursor.setBackground(background);
		}
		
		//字体颜色
		Color foreground = (Color) ResourceManager.createResource(self.getString("foreground"), 
		        "xworker.swt.graphics.Color", "rgb", actionContext);
		if(foreground != null){
		    cursor.setForeground(foreground);
		}
		
		//注册默认选择事件监听
		try{
		    actionContext.push(null).put("cursor", cursor);
		    SwtListener listener = new SwtListener(world.getThing("xworker.swt.custom.TableCursorEditor/@actions/@selectionListener"), actionContext, true);
		    cursor.addListener(SWT.DefaultSelection, listener);
		    cursor.addListener(SWT.Selection, listener);
		    cursor.addListener(SWT.MouseDown, listener);
		}finally{
		    actionContext.pop();
		}
		//为表格注册监听事件
		SwtListener tableListener = new SwtListener(world.getThing("xworker.swt.custom.TableCursorEditor/@actions/@tableSelectionListener"), actionContext, true);
		parent.addListener(SWT.Selection, tableListener);
		parent.setData("tableCursorListener", tableListener);
		
		ControlEditor editor = new ControlEditor(cursor);
		editor.grabHorizontal = true;
		editor.grabVertical = true;
		cursor.setData("editor", editor);
		cursor.setData("thing", self);
		cursor.setData("table", parent);
		
		actionContext.getScope(0).put(self.getString("name"), cursor);
		return cursor;        
	}

	public static void selectionListener(ActionContext actionContext){    	
		//TableCursor
    	Composite cursor = (Composite) UtilEvent.getEventWidget(actionContext);
    	//TableItem
		Item item = (Item) ItemEditorUtils.getCursorRow(cursor);
		//TableColumn
		int column = ItemEditorUtils.getCursorColumn(cursor);
		//CursorThing
		Thing thing = (Thing) cursor.getData("thing");
		//ControlEditor
		ControlEditor editor = (ControlEditor) cursor.getData("editor");
		cursor.setData("oldColumn", column);
		cursor.setData("oldRow", item);
		
		if(editor.getEditor() != null && !editor.getEditor().isDisposed()){
		    //正在编辑取消编辑，先保存已有的值
			Control oldControl = editor.getEditor();
			ActionContext editorContext = (ActionContext) editor.getEditor().getData("editorContext");
			Thing editorThing = (Thing) editor.getEditor().getData("editorThing");
			Object value = editorThing.doAction("getValue", editorContext);
			thing.doAction("setValue", actionContext, 
	 	            UtilMap.toParams(new Object[]{"item", oldControl.getData("item"), 
	 	            		"column", oldControl.getData("column"), "value", value}));			
		    editor.getEditor().dispose();
		}
		
		Event event = actionContext.getObject("event");
		if(event.type == SWT.Selection) {
			thing.doAction("onSelection", actionContext, "item", item, "column", column, "cursor", cursor);
			return;
		}
		
		//寻找适合的ColumnEditor
		Thing editorThing = thing.doAction("getEditorThing", actionContext, "cursorThing", thing, "cursor", cursor, "item", item);
		
		//System.out.println("TableCursorEditorCreator editorThing=" + editorThing);
		if(editorThing != null){
		    ActionContext ac = new ActionContext();
		    DataObject data = (DataObject) item.getData();
		    ac.put("params", data); //下拉框联动等时，会使用data作为查询条件
		    ac.put("parentContext", actionContext);
		    ac.put("parent", cursor);
		    ac.put("cursor", cursor);
		    ac.put("item", item);
		    ac.put("column", column);
		    ac.put("table", cursor.getData("table"));
		    ac.put("cursorThing", thing);
		
		    Control columnEditor = (Control) editorThing.doAction("create", ac);
		    //log.info("columnEditor=" + columnEditor);
		    if(columnEditor != null){
		        Thing editorImplThing = editorThing;//.getChilds().get(0);
		        Thing cursorThing = (Thing) cursor.getData("thing");
		        Object value = cursorThing.doAction("getValue", actionContext, UtilMap.toParams(new Object[]{"item", item, "column", column}));
		        editorImplThing.doAction("setValue", ac, UtilMap.toParams(new Object[]{"value", value}));
		        columnEditor.setData("cursor", cursor);
		        columnEditor.setData("item", item);
		        columnEditor.setData("column", column);
		        columnEditor.setData("editorContext", ac);
		        columnEditor.setData("editorThing", editorImplThing);
		        
		        editor.setEditor(columnEditor);
		        //editor.setData("editorThing", editorThing); //保存事物，留后面setValue和getValue
		        //editor.setData("editorContext", ac);
		        
		        //如果是直接弹出窗口，焦点到弹出窗口上
		        if(columnEditor.getData("window") != null){
		            Shell window = (Shell) columnEditor.getData("window");
		            window.forceActive();
		            window.setFocus();
		        }
		    }
		}  
		
		thing.doAction("onSelection", actionContext, "item", item, "column", column, "cursor", cursor);
	}

    public static void tableSelectionListener(ActionContext actionContext){
		Composite table = (Composite) UtilEvent.getEventWidget(actionContext);
		Composite cursor = (Composite) table.getData("tableCursor");
		
		ControlEditor editor = (ControlEditor) cursor.getData("editor");
		if(editor.getEditor() != null && !editor.getEditor().isDisposed()){
		    if(ItemEditorUtils.getCursorColumn(cursor) != (Integer) cursor.getData("oldColumn") 
		    		|| ItemEditorUtils.getCursorRow(cursor) != cursor.getData("oldRow")){
		        try{
		            //先设置当前编辑器的值，实现编辑器焦点移开后保存编辑结果
		            Thing editorThing = (Thing) editor.getEditor().getData("editorThing");
		            ActionContext editorContext = (ActionContext) editor.getEditor().getData("editorContext");
		            TableItem item = (TableItem) editor.getEditor().getData("item");
		            int column = (Integer) editor.getEditor().getData("column");
		            Object value = editorThing.doAction("getValue", editorContext);        
		            Thing cursorThing = (Thing) cursor.getData("thing");
		            cursorThing.doAction("setValue", actionContext, UtilMap.toParams(new Object[]{"item", item, "column", column, "value", value}));		                
		        }finally{
		        	//销毁编辑器
		            editor.getEditor().dispose();
		        }
		    }
		}       
	}
    
    @SuppressWarnings("unchecked")
	public static Thing getEditorThing1(Thing cursorThing, int column, TableItem item, ActionContext actionContext) {
    	Thing defaultEditorThing = null;
		Thing editorThing = null;

		
		for(Thing child : (List<Thing>) cursorThing.get("ColumnEditor@")){
			String childColumn = child.getString("column");
		    if(childColumn == null || childColumn == ""){
		        defaultEditorThing = child;
		    }else if(childColumn.equals(String.valueOf(column))){
		        editorThing = child;
		        break;
		    }
		}
		
		if(editorThing  == null && defaultEditorThing != null){
		    editorThing = defaultEditorThing;
		}
		
		if(editorThing != null && editorThing.getChilds().size() > 0) {
			return editorThing.getChilds().get(0);
		}else {
			return null;
		}
    }
        
	@ActionParams(names="self,item, column")
    public static Thing getEditorThing(Thing cursorThing, TableItem item, int column, ActionContext actionContext) {
    	return getEditorThing1(cursorThing, column, item, actionContext);
    }

    @SuppressWarnings("unchecked")
	public static void setValue(ActionContext actionContext){
    	Object value = actionContext.get("value");
		
		TableItem item = (TableItem) actionContext.get("item");
		if(item == null){
		    return;
		}
		//item.setText(column, String.valueOf(value));
		    
		Thing self = (Thing) actionContext.get("self");
		if("store".equals(self.getString("dataType"))){
		    //store的有关数据
		    Map<String, Object> record = (Map<String, Object>) item.getData("_store_record");
		    List<Thing> columns = (List<Thing>) item.getParent().getData("_columns");
		    Thing store = (Thing) item.getParent().getData("_store");
		    int column = (Integer) actionContext.get("column");
		    Thing columnAttr = columns.get(column);
		    Object oldValue = null;
		    if(record != null){
		    	oldValue = record.get(columnAttr.getString("name"));
		    	record.put(columnAttr.getString("name"), value);
		    	
		    	if(oldValue != record.get(columnAttr.getString("name"))){
				    //如果数据变动了，把联动的数据清空
				    clearRelationData(columnAttr, record, columns, new ActionContext());
				}
		    }else{
		    	oldValue = item.getText(column);
		    }
		    
		    //先显示一下数据，否则会在编辑光标上显示空
		    Action action = World.getInstance().getAction("xworker.app.view.swt.data.events.TableDataStoreListener/@actions/@getColumnDisplayValue");
		    String displayText = value == null ? "" : value.toString();
		    if(record != null){
		    	displayText = (String) action.run(actionContext, UtilMap.toParams(new Object[]{"column", columnAttr, "record", record}));
		    }
		    if(displayText != null){
		        item.setText(column, displayText);
		    }
		    
		    if(record != null){
		    	store.doAction("update", actionContext, UtilMap.toParams(new Object[]{"record", record}));
		    }
		}else{
		    int column = (Integer) actionContext.get("column");
		    String displayText = value == null ? "" : value.toString();
		    item.setText(column, displayText);
		}
		     
	}

    public static void clearRelationData(Thing column, Map<String, Object> record, List<Thing> columns, ActionContext context){
	    if(context.get(column.getString("name")) != null){
	        //避免递归操作
	        return;
	    }
	    context.put(column.getString("name"), column);
	    
	    String modifyStoreListener =(String) column.getString("modifyStoreListener");
	    if(modifyStoreListener != null && modifyStoreListener != ""){
	        for(String lis : modifyStoreListener.split("[,]")){
	            Thing lisColumn = null;
	            for(Thing col : columns){
	                if(lis.equals(col.getString("name"))){
	                    lisColumn = col;
	                    break;
	                }
	            }
	            
	            if(lisColumn != null){
	                record.put(lisColumn.getString("name"), null);
	                clearRelationData(lisColumn, record, columns, context);
	            }
	        }
	    }
	}   


    @SuppressWarnings("unchecked")
	public static Object getValue(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
		if("store".equals(self.getString("dataType"))){
			TableItem item = (TableItem) actionContext.get("item");
		    Map<String, Object> record =  (Map<String, Object>) item.getData();
		    List<Thing> columns = (List<Thing>) item.getParent().getData("_columns");
		    //Thing store = (Thing) item.getParent().getData("_store");
		    int column = (Integer) actionContext.get("column");
		    Thing columnAttr = columns.get(column);
		    //String disField = columnAttr.getString("displayField");
		    String text = null;
		    /*if(disField != null && disField != ""){
		        def diss = disField.split("[.]");
		        def v = record;
		        try{
		            for(dis in diss){
		                v = v.get(dis);
		                if(v == null){
		                    break;
		                }                        
		            }
		            if(v != null){
		                text = v;
		            }
		        }catch(Exception e){
		            v = null;
		            log.warn("Get display value error," + disFiled, e);
		        }
		    }*/
		    if(text == null){
		    	if(record != null){
		    		Object value = record.get(columnAttr.getString("name"));
		    		if(value != null){
		    			text = String.valueOf(value);
		    		}
		    	}else{
		    		text = item.getText(column);
		    	}
		    }
		    if(text == null){
		        text = "";
		    }else{
		        text = String.valueOf(text);
		    }
		    return text;
		}else{
			TableItem item = (TableItem) actionContext.get("item");
		    return item.getText((Integer) actionContext.get("column"));		    
		}       
	}

}