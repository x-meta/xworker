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
package xworker.swt.xworker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.actions.ActionContainer;

public class ObjectViewerActions {
    public static void setObjects(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Tree dataTree = actionContext.getObject("dataTree");
        List<Map<String, Object>> objects = actionContext.getObject("dataTree");
        ActionContainer actions = actionContext.getObject("actions");
        
        //清除dataTree
        dataTree.removeAll();
        
        //增加子节点
        for(Map<String, Object> data : objects){
            Object object = data.get("object");
            String clsName = object != null ? "" + object.getClass() : "null";
            String value = String.valueOf(object);
            TreeItem treeItem = new TreeItem(dataTree, SWT.NONE);
            treeItem.setText(new String[]{(String) data.get("name"), value, clsName});
            treeItem.setData(object);
            treeItem.setData("name", (String) data.get("name"));
            
            actions.doAction("initDataTree", actionContext, "treeItem",treeItem,
            		"value", object, "context", new HashMap<String, Object>());
        }
    }

}