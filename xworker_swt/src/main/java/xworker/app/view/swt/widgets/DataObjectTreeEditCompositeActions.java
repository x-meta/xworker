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
package xworker.app.view.swt.widgets;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.app.view.swt.data.events.TreeDataStoreListener;
import xworker.dataObject.DataObject;
import xworker.lang.actions.ActionContainer;
import xworker.swt.util.SwtDialog;
import xworker.swt.util.SwtDialogCallback;
import xworker.util.XWorkerUtils;

public class DataObjectTreeEditCompositeActions {
	private static Logger logger = LoggerFactory.getLogger(DataObjectTreeEditCompositeActions.class);
	
    public static Object create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        World world = World.getInstance();
        
        //配置
        Thing dataObject = world.getThing(self.getString("dataObject"));
        if(dataObject == null){
        	Thing dataObjects = self.getThing("dataObjects@0");
            if(dataObjects != null && dataObjects.getChilds().size() > 0){
                dataObject = dataObjects.getChilds().get(0);
            }
        }
        if(dataObject == null){
            logger.warn("QueryTalbeComposite: dataObject is null, dataObject=" + self.getString("dataObject"));
            return null;
        }
        
        Thing queryDataObject = world.getThing(self.getString("queryDataObject"));
        if(queryDataObject == null){
        	Thing queryDataObjects = self.getThing("queryDataObjects@0");
            if(queryDataObjects != null && queryDataObjects.getChilds().size() > 0){
                queryDataObject = queryDataObjects.getChilds().get(0);
            }
            //logger.info("query=" + queryDataObject + ",queryDataObjects=" + queryDataObjects);
        }
        
        if(queryDataObject == null){
            
        	Thing queryDataObjects = dataObject.getThing("QueryFormDataObject@0");
            if(queryDataObjects != null && queryDataObjects.getChilds().size() > 0){
                queryDataObject = queryDataObjects.getChilds().get(0);
            }
        }
        
        if(queryDataObject == null){
            queryDataObject = dataObject;
        }
        Thing queryConfig = world.getThing(self.getString("queryConfig"));
        if(queryConfig == null){
            queryConfig = self.getThing("queryConfig@0");
        }
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("dataObject", dataObject.getMetadata().getPath());
        data.put("label", dataObject.getMetadata().getLabel());
        if(queryConfig != null){
            data.put("queryConfig", queryConfig.getMetadata().getPath());
        }else{
            data.put("queryConfig", dataObject.getStringBlankAsNull("defaultQueryConfig"));
            if(data.get("queryConfig") == null){
                queryConfig = dataObject.getThing("Condition@0");
                if(queryConfig != null){
                    data.put("queryConfig", queryConfig.getMetadata().getPath());
                }        
            }
            
            if(data.get("queryConfig") == null){
                data.put("queryConfig", "");
            }
        }
        data.put("queryDataObject", queryDataObject.getMetadata().getPath());
        data.put("queryButton", self.getBoolean("queryButton"));
        data.put("newButton", self.getBoolean("newButton"));
        data.put("editButton", self.getBoolean("editButton"));
        data.put("editBatchButton", self.getBoolean("editBatchButton"));
        data.put("deleteButton", self.getBoolean("deleteButton"));
        data.put("pagingToolbar", self.getBoolean("pagingToolbar"));
        data.put("tableCheck", self.getString("treeCheck"));
        data.put("queryFormEditCols", self.getString("queryFormEditCols"));
        data.put("selectAllButton", self.getBoolean("selectAllButton"));
        data.put("selectRerverseButton", self.getBoolean("selectRerverseButton"));
        data.put("idRootValue", self.get("idRootValue"));
        data.put("idName", self.get("idName"));
        data.put("parentIdName", self.get("parentIdName"));
        data.put("addChildButton", self.getBoolean("addChildButton"));
        
        //Thing buttons = null;//self.getThing("Buttons@0");
       // if(buttons != null){
        //    data.put("buttons", buttons.getChilds());
        //}else{
            data.put("buttons", Collections.EMPTY_LIST);
        //}
        
        //logger.info("data=" + data);
        //通过模板生成Composite
        Thing template = world.getThing("xworker.app.view.swt.widgets.DataObjectTreeEditComposite/@template");
        ActionContext ac = new ActionContext();
        ac.put("data", data);
        Thing compositeThing = ((Thing) template.doAction("process", ac)).getChilds().get(0);
        if(compositeThing != null){
            if(self.getBoolean("tableCheck")){
            }
            if(self.getBoolean("debug")){
                compositeThing.set("label", self.getMetadata().getLabel());
                XWorkerUtils.ideOpenThing(compositeThing);
            }
            ActionContext dataObjectContext = actionContext;    
            if(self.getStringBlankAsNull("actionContext") != null){
                dataObjectContext = new ActionContext();
                dataObjectContext.put("parent", actionContext.get("parent"));
                dataObjectContext.put("parentContext", actionContext);
                dataObjectContext.getScope(0).put("editorThing", self);
                actionContext.getScope(0).put(self.getString("actionContext"), dataObjectContext);
            }
            Composite composite = (Composite) compositeThing.doAction("create", dataObjectContext);
            try{
                Bindings bindings = actionContext.push(null);
                bindings.put("parent", composite);
                for(Thing child : self.getChilds()){
                    child.doAction("create", actionContext);
                }
                
                bindings.put("parent", dataObjectContext.get("buttonComposite"));
                for(Thing child : self.getChilds("Buttons")){
                    for(Thing c : child.getChilds()){
                        c.doAction("create", actionContext);
                    }
                }
            }finally{
                actionContext.pop();
            }
            
            return composite;
        }else{
            return null;
        }
    }

    public static void selectAllButton(ActionContext actionContext){
        //Thing self = actionContext.getObject("self");
        Tree dataTree = actionContext.getObject("dataTree");
        
        for(TreeItem item : dataTree.getItems()){
           item.setChecked(true);
        }
    }

    public static void selectRerverseButton(ActionContext actionContext){
    	Tree dataTree = actionContext.getObject("dataTree");
        for(TreeItem item : dataTree.getItems()){
           item.setChecked(item.getChecked() ? false : true);
        }
    }

    @SuppressWarnings("unchecked")
	public static void addChildItem(final ActionContext actionContext){
        //Thing self = actionContext.getObject("self");
        final Tree dataTree = actionContext.getObject("dataTree");
        final Thing dataStore = actionContext.getObject("dataStore");
        Thing editorThing = actionContext.getObject("editorThing");
        World world = World.getInstance();
        
        //Display display = dataTree.getDisplay();
        Shell parent = dataTree.getShell();
                 
        //获取要编辑的数据对象
        final TreeItem parentTreeItem = dataTree.getSelection()[0];
        Map<String, Object> parentData = (Map<String, Object>) parentTreeItem.getData();
        
        String title = "添加子节点";
               
        try{
            Bindings bindings = actionContext.push(null);
            bindings.put("dataObject", dataStore.get("dataObject"));
         
            //打开编辑对话框
            ActionContext ac = new ActionContext();
            ac.put("parent", parent);    
            ac.put("title", title);
            ac.put("message", "添加子节点。");
            ac.put("insert", true);
            ac.put("continueEdit", false);
            ac.put("dataStore", dataStore);
            ac.put("parentContext", actionContext);
            ac.put("initDataObject", new DataObject((Thing) dataStore.get("dataObject")));
            ac.put("confirm", false);
            String idName = editorThing.getString("idName");
            String parentIdName = editorThing.getString("parentIdName");
            Map<String, Object> values = new HashMap<String, Object>();
            values.put(parentIdName, parentData.get(idName));
            ac.put("initValues", values);
            
            //logger.info("initValues=" + ac.get("initValues"));
            Thing dialogThing = world.getThing("xworker.app.view.swt.data.dialogs.AddChildDataObjectDialog");
            Shell shell = (Shell) dialogThing.doAction("create", ac);
            if(title != null){
                shell.setText(title);
            }
            ((ActionContainer) ac.get("actions")).doAction("setDataObject", ac, "dataObject", dataStore.get("dataObject"));
            SwtDialog dialog = new SwtDialog(shell, ac);
            dialog.open(new SwtDialogCallback() {

				@Override
				public void disposed(Object result) {
					Map<String, Object> dataObject = (Map<String, Object>) result;     
		            if(dataObject != null){
		            	DataObject db = new DataObject((Thing) dataStore.get("dataObject"));
		                db.putAll(dataObject);
		                db = db.create(actionContext);
		                if(db != null){
		                    actionContext.peek().put("self", dataTree.getData("storeListener"));
		                    actionContext.peek().put("record", db);
		                    String[] texts = TreeDataStoreListener.recordToRowTexts(actionContext);
		                    
		        		    TreeItem treeItem = new TreeItem(parentTreeItem, SWT.NONE);
		                    treeItem.setText(texts);
		                    treeItem.setData(db);
		                    treeItem.setData("_store_record", db);
		                }
		            }  
				}
            	
            });
            
        }catch(Exception e){
            logger.error("add child error", e);
        }finally{
            actionContext.pop();
        }
    }

    public static void cascadeCheck(ActionContext actionContext){
        Event event = actionContext.getObject("event");
        Thing editorThing = actionContext.getObject("editorThing");
        
        if (event.detail == SWT.CHECK && editorThing.getBoolean("checkCascade")){
            checkParent((TreeItem) event.item, ((TreeItem) event.item).getChecked());
            checkChilds((TreeItem) event.item, ((TreeItem) event.item).getChecked());
        }
    }
    
    public static void checkParent(TreeItem item, boolean check){
        TreeItem p = item.getParentItem();
        
        if(p != null){
            p.setChecked(check);
            checkParent(p, check);
        }   
    }
    
    public static void  checkChilds(TreeItem item , boolean check){
        for(TreeItem childItem : item.getItems()){
            childItem.setChecked(check);
            
            checkChilds(childItem, check);
        }
    }

    public static void insertDataObject(ActionContext actionContext){
        //Thing self = actionContext.getObject("self");
        Tree dataTree = actionContext.getObject("dataTree");
        Object dataObject = actionContext.get("dataObject");
        
        //获取要编辑的数据对象
        Object parentTreeItem = dataTree;
        if(dataTree.getSelection() != null && dataTree.getSelection().length > 0){
            parentTreeItem = dataTree.getSelection()[0];
        }
               
        try{
            if(actionContext.get("dataObject") != null){
                actionContext.peek().put("self", dataTree.getData("storeListener"));
                actionContext.peek().put("record", dataObject);
                String[] texts = TreeDataStoreListener.recordToRowTexts(actionContext);
                                
        	    TreeItem treeItem = null;
        	    if(parentTreeItem instanceof Tree){
        	    	treeItem = new TreeItem((Tree) parentTreeItem, SWT.NONE);
        	    }else{
        	    	treeItem = new TreeItem((TreeItem) parentTreeItem, SWT.NONE);
        	    }
                treeItem.setText(texts);
                treeItem.setData(dataObject);
                treeItem.setData("_store_record", dataObject);
            }
        }catch(Exception e){
            logger.error("add child error", e);
        }
    }

}