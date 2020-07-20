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
package xworker.swt.xwidgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.lang.actions.ActionContainer;
import xworker.util.XWorkerUtils;

public class DemoThingActions {
	private static Logger logger = LoggerFactory.getLogger(DemoThingActions.class);
	
    public static void Listener(ActionContext actionContext){
        //Thing self = actionContext.getObject("self");
        Thing thing = actionContext.getObject("thing");
        Event event = actionContext.getObject("event");
        TabItem descExtendsTabItem = actionContext.getObject("descExtendsTabItem");
        Table descTable = actionContext.getObject("descTable");
        Table extTable = actionContext.getObject("extTable");
        
        if(actionContext.get("descExtendsTabItem") != null && event.item == descExtendsTabItem){
            //每次都重新刷新
        	descTable.removeAll();
        	extTable.removeAll();
            
            for(Thing desc : thing.getAllDescriptors()){
                String path = desc.getMetadata().getPath();
                TableItem item = new TableItem(descTable, SWT.NONE);
                item.setData(path);
                item.setText(new String[]{path});        
            }
            
            for(Thing ext : thing.getAllExtends()){
                String path = ext.getMetadata().getPath();
                TableItem item = new TableItem(descTable, SWT.NONE);
                item.setData(path);
                item.setText(new String[]{path});        
            }
        }
    }

    public static void updateButtonSelection(ActionContext actionContext){
    	ActionContext thingEditor = actionContext.getObject("thingEditor");
    	Thing thing = actionContext.getObject("thing");
        //Event event = actionContext.getObject("event");
        TabItem demoTabItem = actionContext.getObject("demoTabItem");
        TabFolder demoTabFolder = actionContext.getObject("demoTabFolder");
        
        //先保存事物
    	ActionContainer editorActions = thingEditor.getObject("editorActions");
        editorActions.doAction("save");
        
        //在编辑器中编辑事物
        editorActions.doAction("setThing", actionContext, "thing",  thing);
        
        demoTabItem.setText(thing.getMetadata().getLabel());
        demoTabFolder.setSelection(0);
    }

    public static void tableDefaultSelection(ActionContext actionContext){
        //Thing self = actionContext.getObject("self");
        World world = World.getInstance();
        Event event = actionContext.getObject("event");
        
        Thing thing = (Thing) world.getThing((String) event.item.getData());
        XWorkerUtils.ideOpenThing(thing);
    }

    public static void setThing(ActionContext actionContext){
        Object thing = actionContext.getObject("thing");
        ActionContext thingEditor = actionContext.getObject("thingEditor");
        ActionContainer editorActions = thingEditor.getObject("editorActions");
        ActionContext descThingEditor = actionContext.getObject("descEditor");
        ActionContainer descEditorActions = descThingEditor.getObject("editorActions");
        
        if(thing instanceof String){
            thing = World.getInstance().getThing((String) thing);
        }
        
        if(thing == null){
            logger.warn("DemoThing: thing is null");
            return;
        }
        
        //在编辑器中编辑事物
        editorActions.doAction("setThing", actionContext, "thing", thing);
        
        //在描述者里显示事物
        Thing desc = ((Thing) thing).getDescriptor();
        Thing ext = desc.getExtends().size() > 0 ? desc.getExtends().get(0) : null;
        if(ext != null && desc.getChilds("attribute").size() == 0) {
        	desc = ext;
        }
        descEditorActions.doAction("setThing", actionContext, "thing", desc);
        
        actionContext.getScope(0).put("thing", thing);
    }

}