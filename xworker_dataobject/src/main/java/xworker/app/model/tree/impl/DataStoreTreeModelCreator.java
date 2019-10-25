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
package xworker.app.model.tree.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;
import xworker.app.model.tree.TreeModelUtil;

public class DataStoreTreeModelCreator {
    @SuppressWarnings("unchecked")
	public static void create(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");
        
        //创建树模型实例，树模型实例也是事物，结构为本事物，其中属性modelThing引用本事物
        Thing model = new Thing();
        model.put("descriptors", "xworker.app.model.tree.TreeModelInsAction");
        model.put("extends", "xworker.app.model.tree.TreeModelInsAction");
        model.put("modelThing", self);
        model.put("listeners", new ArrayList<Thing>());
        
        //create方法一般在swt里使用，看看是否要绑定到父节点
        if(self.getBoolean("bindToParent")){
            Object parent = actionContext.get("parent");
            if(parent != null){
                //if(parent instanceof Tree || parent instanceof TreeItem){
                    //绑定到Tree或者TreeItem，通过注册监听的方式绑定，监听的init完成创建节点等等事务
                    Thing listener = new Thing("xworker.app.model.tree.swt.TreeModelTreeListener");
                    ((List<Thing>) model.get("listeners")).add(listener);
                    listener.put("treeModel", model);
                    //listener.doAction("init", actionContext, ["treeModel":model, "parent":parent]);
                //}
            }
        }
        
        //数据仓库
        String dataStoreStr = self.getStringBlankAsNull("dataStore");
        if(dataStoreStr != null){
	        Thing dataStore = (Thing) OgnlUtil.getValue(self, "dataStore", dataStoreStr, actionContext);
	        if(dataStore != null){
	            dataStore.doAction("addListener", actionContext, UtilMap.toMap(new Object[]{"listener", model}));
	        }
        }
        
        //保存到变量上下文中
        actionContext.getScope(0).put(self.getString("name"), model);
    }

    @SuppressWarnings("unchecked")
	public static Object getRoot(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");
    	
        List<Object> list = (List<Object>) self.get("records");
        if(list == null){
            return null;
        }else{
            for(Object l : list){
                Object idValue = OgnlUtil.getValue(self.getString("idField"), l);
                if(idValue != null && idValue.toString().equals(self.getString("rootIdValue"))){
                    return self.doAction("dataToNode", actionContext, UtilMap.toMap(new Object[]{"data",l}));
                }
            }
            
            return null;
        }
    }

    @SuppressWarnings("unchecked")
	public static Object getChilds(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");
        
        List<Object> list = (List<Object>) self.get("records");
        if(list == null){
            return null;
        }else{
            //log.info("id=" + actionContext.get("id"));
            if(actionContext.get("id") == null){
                return self.doAction("getRoots", actionContext);
            }
        
            List<Object> childs = new ArrayList<Object>();
            for(Object l : list){
                Object idValue = OgnlUtil.getValue(self.getString("parentIdField"), l);
                if(idValue != null && idValue.toString().equals(actionContext.get("id").toString())){
                    Object node = self.doAction("dataToNode", actionContext, UtilMap.toMap(new Object[]{"data", l}));
                    if(node != null){
                        childs.add(node);
                    }  
                }
            }
            
            //log.info("childs=" + childs);
            return childs;
        }
    }

    public static Object dataToNode(ActionContext actionContext) throws OgnlException{
    	Thing self = (Thing) actionContext.get("self");
        Object data = actionContext.get("data");
        
        Map<String, Object> node = new HashMap<String, Object>();
        TreeModelUtil.setAttributes(self, OgnlUtil.getValue(self, "idField", data), node);
        node.put("text", OgnlUtil.getValue(self.getString("textField"), data));
        String iconField = self.getString("iconField");
        if(iconField != null && !"".equals(iconField)){
            node.put("icon", OgnlUtil.getValue(self.getString("iconField"), data));
        }
        String clsField = self.getString("clsField");
        if(clsField != null && !"".equals(clsField)){
            node.put("cls", OgnlUtil.getValue(self.getString("clsField"), data));
        }
        node.put("data", data);
        
        return node;
    }

    public static void beforeLoad(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
        //清空数据
        self.put("records", new ArrayList<Object>());
        
        //触发初始化事件
        self.doAction("fireEvent", actionContext, UtilMap.toMap(new Object[]{"eventName", "init"}));
    }

    public static void onLoaded(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
    	Thing store = (Thing) actionContext.get("store");
        //清空数据
        self.put("records", store.get("records"));
        
        //触发初始化事件
        self.doAction("fireEvent", actionContext, UtilMap.toMap(new Object[]{"eventName", "init"}));
    }

}