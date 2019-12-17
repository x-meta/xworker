/*
 * Copyright 2007-2016 The xworker.org.
 *
 * Licensed to the X-Meta under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The X-Meta licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xworker.app.view.swt.data;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilString;

import xworker.dataObject.DataObject;
import xworker.swt.util.SwtUtils;

public class DataStoresActions {
    public static void create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }

    public static void createTableItemOperatorToolbar(ActionContext actionContext) {
    	DataObject theData = actionContext.getObject("theData");
    	TableItem item = actionContext.getObject("tableItem");
    	Thing self = actionContext.getObject("self");
    	
    	String columnName = self.doAction("getColumnName", actionContext);
    	if(columnName ==null || "".equals(columnName)) {
    		return;
    	}
    	
    	int columnIndex = -1;
    	TableColumn[] columns = item.getParent().getColumns();
    	for(int i=0; i<columns.length; i++) {
    		if(columnName.equals(columns[i].getData("name"))){
    			columnIndex = i;
    			break;
    		}
    	}
    	
    	if(columnIndex == -1) {
    		return;
    	}
    	
    	World world = World.getInstance();
   
    	//使用TableEditor
    	final TableEditor editor = new TableEditor(item.getParent());
    	editor.horizontalAlignment = SWT.CENTER;
    	editor.verticalAlignment = SWT.CENTER;
        editor.grabHorizontal = true;
        editor.minimumWidth = 50;
    	ActionContext ac = new ActionContext();
    	ac.put("parent", item.getParent());
    	//创建Toolbar
    	Thing toolBarThing = world.getThing("xworker.app.view.swt.data.prototypes.TableItemOperator/@toolbarComposite");
    	final Composite composite = toolBarThing.doAction("create", ac);
    	ToolBar control = ac.getObject("toolBar");
    	ac.peek().put("parent", control);
    	//创建条目
    	if(self.getBoolean("edit")) {
    		Thing editItem = world.getThing("xworker.app.view.swt.data.prototypes.TableItemOperator/@toolBar1/@editItem");
    		createOperatorToolItem(item, theData, editItem, ac);
    	}
    	
    	if(self.getBoolean("delete")) {
    		Thing editItem = world.getThing("xworker.app.view.swt.data.prototypes.TableItemOperator/@toolBar1/@deleteItem");
    		createOperatorToolItem(item, theData, editItem, ac);
    	}
    	
    	for(Thing items : self.getChilds("ToolItems")) {
    		for(Thing toolItem : items.getChilds()) {
    			createOperatorToolItem(item, theData, toolItem, ac);
    		}
    		
    	}
    	
    	control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
    	control.pack();
    	control.layout();

    	editor.setEditor(composite, item, columnIndex);    	
     	item.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				try {
					composite.dispose();
					editor.dispose();					
				}catch(Exception ee) {
					
				}
			}
    		
    	});
    }
    
    private static void createOperatorToolItem(TableItem tableItem, DataObject theData, Thing itemThing, ActionContext actionContext) {
    	ToolItem toolItem = itemThing.doAction("create", actionContext);
    	toolItem.setData(theData);
    	toolItem.setData("tableItem", tableItem);
    }
    
    public static void tableItemOperatorEdit(ActionContext actionContext) {
    	Event event = actionContext.getObject("event");
    	DataObject theData = (DataObject) event.widget.getData();
    	TableItem tableItem = (TableItem) event.widget.getData("tableItem");
    	Thing dataStore = (Thing) tableItem.getData("dataStore");
    	
    	ActionContext ac = new ActionContext();
    	ac.put("parent", tableItem.getParent().getShell());    
    	ac.put("title", theData.getMetadata().getDescriptor().getMetadata().getLabel());
    	ac.put("message", "");;
    	ac.put("confirm", "false");
    	ac.put("dataStore", dataStore);
    	ac.put("parentContext", actionContext);
    	ac.put("dataObject", theData);
    	Thing dialogThing = World.getInstance().getThing("xworker.app.view.swt.data.dialogs.EditDataObjectDialog/@shell");
    	Shell shell = dialogThing.doAction("create", ac);
    	if(theData != null){
    	    Thing dataObject = theData.getMetadata().getDescriptor();
    	    int editDialogWith = dataObject.getInt("editDialogWith");
    	    int editDialogHeight = dataObject.getInt("editDialogHeight");
    	    if(editDialogWith != 0 && editDialogHeight != 0){
    	        shell.setSize(editDialogWith, editDialogHeight);
    	    }else{
    	        shell.pack();
    	    }
    	}
    	SwtUtils.centerShell(shell);
    	shell.setVisible(true);
    }
    
    public static void tableItemOperatorDelete(ActionContext actionContext) {
    	Thing action = new Thing("xworker.app.view.swt.data.DataStoreActions/@DeleteDataObject");
    	Event event = actionContext.getObject("event");
    	DataObject theData = (DataObject) event.widget.getData();
    	TableItem tableItem = (TableItem) event.widget.getData("tableItem");
    	Thing dataStore = (Thing) tableItem.getData("dataStore");
    	
    	action.put("dataStore", "var:dataStore");
    	action.put("dataObjects", "var:dataObjects");
    	//action.put("controlName", "table");
    	action.put("title", theData.getMetadata().getDescriptor().getMetadata().getLabel());
    	action.put("deleteConfirmMessage", UtilString.getString("lang:d=确认要删除这一行数据么?&en=Are you sure to delete the data?", actionContext));
    	List<DataObject> dataObjects = new ArrayList<DataObject>();
    	dataObjects.add(theData);
    	action.getAction().run(actionContext, "dataStore", dataStore, "dataObjects", dataObjects);
    }
}