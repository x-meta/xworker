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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.dataObject.DataObject;
import xworker.swt.design.Designer;
import xworker.swt.util.SwtUtils;
import xworker.util.XWorkerUtils;

public class DataObjectEditCompoisteActions {
    public static Object create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        World world = World.getInstance();
        
        //创建一个空的布局
        Composite parent = actionContext.getObject("parent");
        
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new FillLayout());
        
        //创建事物
        Thing thing = self.detach();
        
        //变量上下文
        ActionContext dataObjectContext = new ActionContext();
        dataObjectContext.put("parent", composite);
        dataObjectContext.put("parentContext", actionContext);
        dataObjectContext.getScope(0).put("editorThing", self);
        if(self.getStringBlankAsNull("actionContext") != null){
            actionContext.getScope(0).put(self.getString("actionContext"), dataObjectContext);
        }
        
        //println("create:" + dataObjectContext.hashCode());
        thing.setData("actionContext", dataObjectContext);
        thing.setData("composite", composite);
        thing.setData("parentContext", actionContext);
        thing.setData("self", self);
        
        try{
            Bindings bindings = actionContext.push(null);
            bindings.put("parent", composite);
            for(Thing child : self.getChilds()){
                child.doAction("create", actionContext);
            }
        }finally{
            actionContext.pop();
        }
        
        //配置
        Thing dataObject = (Thing) self.doAction("getDataObject", actionContext);
        /*
        Thing dataObject = world.getThing(self.getString("dataObject"));
        if(dataObject == null){
            Thing dataObjects = self.getThing("dataObjects@0");
            if(dataObjects != null && dataObjects.getChilds().size() > 0){
                dataObject = dataObjects.getChilds().get(0);
            }
        }*/
        if(dataObject == null){
            //log.warn("QueryTalbeComposite: dataObject is null, dataObject=" + self.dataObject);
        }else{
            thing.doAction("setDataObject", actionContext, "dataObject", dataObject);
        }
        
        actionContext.getScope(0).put(self.getMetadata().getName(), thing);
        
        Designer.attachCreator(composite, self.getMetadata().getPath(), actionContext);
        Designer.pushCreator(self);
		try{
			Designer.attach(composite, self.getMetadata().getPath(), actionContext);
		}finally {
			Designer.popCreator();
		}
        //println("create2:" + dataObjectContext.hashCode());
        return composite;
    }

    public static void selectAllButton(ActionContext actionContext){
        Table dataTable = actionContext.getObject("dataTable");
        
        for(TableItem item : dataTable.getItems()){
           item.setChecked(true);
        }
    }

    public static void selectRerverseButton(ActionContext actionContext){
    	Table dataTable = actionContext.getObject("dataTable");
        for(TableItem item : dataTable.getItems()){
           item.setChecked(item.getChecked() ? false : true);
        }
    }

    public static void setDataObject(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        World world = World.getInstance();
        Thing dataObject = actionContext.getObject("dataObject");
        
        Thing thing = self;
        self = (Thing)self.getData("self");
        
        Designer.pushCreator(self);
        try{
	        Thing queryDataObject = (Thing) self.doAction("getQueryDataObject", actionContext);
	        /*
	        Thing queryDataObject = world.getThing(self.getString("queryDataObject"));
	        if(queryDataObject == null){
	            Thing queryDataObjects = self.getThing("queryDataObjects@0");
	            if(queryDataObjects != null && queryDataObjects.getChilds().size() > 0){
	                queryDataObject = queryDataObjects.getChilds().get(0);
	            }
	            //log.info("query=" + queryDataObject + ",queryDataObjects=" + queryDataObjects);
	        }*/
	        
	        if(queryDataObject == null){    
	            Thing queryDataObjects = dataObject.getThing("QueryFormDataObject@0");
	            if(queryDataObjects != null && queryDataObjects.getChilds().size() > 0){
	                queryDataObject = queryDataObjects.getChilds().get(0);
	            }
	        }
	        
	        if(queryDataObject == null){
	            queryDataObject = dataObject;
	        }
	        Thing queryConfig = (Thing) self.doAction("getQueryConfig", actionContext);
	        /*
	        Thing queryConfig = world.getThing(self.getString("queryConfig"));
	        if(queryConfig == null){
	            queryConfig = self.getThing("queryConfig@0");
	        }*/
	        Map<String, Object> data = new HashMap<String, Object>();
	        data.put("dataObject", dataObject.getMetadata().getPath());
	        data.put("label", dataObject.getMetadata().getLabel());
	        if(queryConfig != null){
	            data.put("queryConfig", queryConfig.getMetadata().getPath());
	        }else{
	            data.put("queryConfig",  dataObject.getStringBlankAsNull("defaultQueryConfig"));
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
	        data.put("toolsButton", self.getBoolean("toolsButton"));
	        data.put("pagingToolbar", self.getBoolean("pagingToolbar"));
	        data.put("tableCheck", self.getString("tableCheck"));
	        data.put("queryFormEditCols", self.getString("queryFormEditCols"));
	        data.put("selectAllButton", self.getBoolean("selectAllButton"));
	        data.put("selectRerverseButton", self.getBoolean("selectRerverseButton"));
	        data.put("newDataObjectInitValues", self.getString("newDataObjectInitValues"));
	        data.put("setQueryButton", self.getBoolean("setQueryButton"));
	        data.put("autoLoad", dataObject.getBoolean("storeAutoLoad"));
	        data.put("autoSave", dataObject.getBoolean("storeAutoSave"));
	        data.put("displayInfo", dataObject.getBoolean("paging_displayInfo"));
	        Thing buttons = null;//self.getThing("Buttons@0");
	       // if(buttons != null){
	            //data.put("buttons", buttons.getChilds());
	        //}else{
	            data.put("buttons", Collections.EMPTY_LIST);
	        //}
	        
	        //log.info("data=" + data);
	        //通过模板生成Composite
	        Thing template = world.getThing("xworker.app.view.swt.widgets.DataObjectEditCompoiste/@template");
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
	            ActionContext dataObjectContext = (ActionContext) thing.getData("actionContext");  
	            //情况原有的内容
	            Composite composite = (Composite) thing.getData("composite");  
	            for(Control child : composite.getChildren()){
	                child.dispose();
	            }
	            dataObjectContext.peek().put("parent", composite);
	            dataObjectContext.getScope(0).put("parentContext", thing.getData("parentContext"));
	            
	            //println("setDAtaObject:" + dataObjectContext.hashCode());
	            ActionContext parentContext = (ActionContext) thing.getData("parentContext");
	            try {
	            	SwtUtils.pushInitStyle();
	            	if(SwtUtils.isRWT()) {
	            		//RWT下显示表格的边框，要不太难看
	            		Thing table = compositeThing.getThing("Table@0");
	            		if(table != null) {
	            			SwtUtils.setInitStyle(table.getMetadata().getPath(), SWT.BORDER);
	            		}
	            	}
	            	
	            	compositeThing.doAction("create", dataObjectContext);
	            	
	                Bindings bindings = parentContext.push(null);
	                bindings.put("parent", dataObjectContext.get("buttonComposite"));
	                for(Thing child : self.getChilds("Buttons")){
	                    for(Thing c : child.getChilds()){
	                        c.doAction("create", parentContext);
	                    }
	                }
	                
	                composite.layout();
	            }finally{
	                parentContext.pop();
	                SwtUtils.popInitStyle();
	            }
	        }
	    }finally {
	    	 Designer.popCreator();
	    }
    }

    public static void setQueryFormValues(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        //数据对象编辑器所在变量上下文
        ActionContext objActionContext = (ActionContext) self.getData("actionContext");
        Thing queryForm = (Thing) objActionContext.get("queryForm");
        
        //调用表单的设置值
        Object values = actionContext.get("values");
        if(queryForm != null) {
        	queryForm.doAction("setValues", objActionContext, "values", values);
        }
        objActionContext.g().put("queryParams", values);
        Thing store = (Thing) objActionContext.get("dataStore");
        store.put("params", values);
    }

    public static List<DataObject> getSelection(ActionContext actionContext){
    	Thing self = actionContext.getObject("self");
        //数据对象编辑器所在变量上下文
        ActionContext objActionContext = (ActionContext) self.getData("actionContext");
        Table dataTable = objActionContext.getObject("dataTable");
        List<DataObject> datas = new ArrayList<DataObject>();
        boolean check = (dataTable.getStyle() & SWT.CHECK) == SWT.CHECK;
        if(check) {
	        for(TableItem item : dataTable.getItems()) {
	        	if(item.getChecked()) {
	        		datas.add((DataObject) item.getData());
	        	}
	        }
        }else {
        	for(TableItem item : dataTable.getSelection()) {
        		datas.add((DataObject) item.getData());
        	}
        }
        
        return datas;
    }
    
    public static Object getQueryFormValues(ActionContext actionContext) {
    	Thing self = actionContext.getObject("self");
    	
    	 ActionContext objActionContext = (ActionContext) self.getData("actionContext");
         Thing queryForm = (Thing) objActionContext.get("queryForm");
         return queryForm.doAction("getValues", actionContext);
    }
    
    public static void doQuery(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        //数据对象编辑器所在变量上下文
        ActionContext objActionContext = (ActionContext) self.getData("actionContext");
        Thing queryForm = (Thing) objActionContext.get("queryForm");
        Thing store = (Thing) objActionContext.get("dataStore");
        
        Object params = null;
        if(queryForm != null){
        	params = queryForm.doAction("getValues", objActionContext);
        }else {
        	params = objActionContext.get("queryParams");
        }
        store.doAction("load", objActionContext, "params", params);
    }

}
    