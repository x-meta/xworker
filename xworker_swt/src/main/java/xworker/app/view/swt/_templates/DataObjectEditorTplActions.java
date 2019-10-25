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
package xworker.app.view.swt._templates;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class DataObjectEditorTplActions {
    public static void queryButtonSelection(ActionContext actionContext){
        //Thing self = actionContext.getObject("self");
        Button editButton = actionContext.getObject("editButton");
        Button deleteButton = actionContext.getObject("deleteButton");
        Thing queryForm = actionContext.getObject("queryForm");
        Thing store = actionContext.getObject("store");
        
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        
        Object params = queryForm.doAction("getValues", actionContext);
        store.doAction("load", actionContext, "params", params);
    }

    public static void createButtonSelection(ActionContext actionContext){
        //Thing self = actionContext.getObject("self");
        Thing store = actionContext.getObject("store");
        store.doAction("openCreateForm", actionContext);
    }

    public static void editButtonAction(ActionContext actionContext){
        //Thing self = actionContext.getObject("self");
        Table dataTable = actionContext.getObject("dataTable");
        Shell shell = actionContext.getObject("shell");
        Thing store = actionContext.getObject("store");
        
        TableItem[] items = dataTable.getSelection();
        if(items == null || items.length == 0){
            MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
            box.setText("警告");
            box.setMessage("请先选择一条记录！");
            box.open();
            return;
        }
        
        store.doAction("openEditForm", actionContext, "record", items[0].getData());
    }

    public static void deleteButtonAction(ActionContext actionContext){
    	Table dataTable = actionContext.getObject("dataTable");
        Shell shell = actionContext.getObject("shell");
        Thing store = actionContext.getObject("store");
        
        TableItem[] items = dataTable.getSelection();
        if(items == null || items.length == 0){
            MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
            box.setText("警告");
            box.setMessage("请先选择一条记录！");
            box.open();
            return;
        }
        
        MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
        box.setText("警告");
        box.setMessage("确认要删除选择的记录么！");
        if(SWT.OK == box.open()){
            store.doAction("remove", actionContext, actionContext.get("record"), items[0].getData());
        }
    }

    public static void editButtonAction1(ActionContext actionContext){
    	Button editButton = actionContext.getObject("editButton");
        Button deleteButton = actionContext.getObject("deleteButton");
        
        editButton.setEnabled(true);
        deleteButton.setEnabled(true);
    }

}