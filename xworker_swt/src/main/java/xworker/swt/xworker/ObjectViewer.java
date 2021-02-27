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
package xworker.swt.xworker;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ExceptionUtil;
import org.xmeta.util.UtilMap;

import xworker.lang.executor.Executor;
import xworker.swt.ActionContainer;
import xworker.swt.design.Designer;

public class ObjectViewer {
	private static final String TAG = ObjectViewer.class.getName();
	
	 public static Object create(ActionContext actionContext){
        World world = World.getInstance();
        Thing self = (Thing) actionContext.get("self");
		
		ActionContext ac = new ActionContext();
		ac.put("parent", actionContext.get("parent"));
		ac.put("params", actionContext.get("params"));
		ac.put("parentActionContext", actionContext);
		ac.put("parentContext", actionContext);
					
		//创建面板
		Thing compositeThing = world.getThing("xworker.swt.xworker.ObjectViewer/@composite");
		Composite composite = null;
		Designer.pushCreator(self);
		try{
			composite = (Composite) compositeThing.doAction("create", ac);
		}finally{
			Designer.popCreator();
		}
		Thing form = new Thing("xworker.swt.xworker.ObjectViewer");
		form.setData("composite", composite);
		form.setData("parentActionContext", actionContext);
		form.set("extends", self.getMetadata().getPath());
		form.setData("actionContext", ac);
		composite.setData("thing", form);
		
		try{
		    //创建子事物，通常是布局数据
		    Bindings bindings = actionContext.push(null);
		    bindings.put("parent", composite);
		    for(Thing child : self.getAllChilds()){
		        child.doAction("create", actionContext);
		    }
		}finally{
			actionContext.pop();
		}
		
		//创建事物定义
		ac.getScope(0).put("self", form);
		
		//设置对象如果存在
		Object obj = self.doAction("getObject", actionContext);
		if(obj != null) {
			form.doAction("setObject", actionContext, "object", obj);
		}
		
		actionContext.getScope(0).put(self.getString("name"), form);
		
		Designer.attachCreator(composite, self.getMetadata().getPath(), actionContext);
		return composite;        
	}
	 
	 public static void setObject(ActionContext actionContext){
		 Thing self = (Thing) actionContext.get("self");
		 Object object = actionContext.get("object");
		 
		 ActionContext ac = (ActionContext) self.getData("actionContext");
		 ((ActionContainer) ac.get("actions")).doAction("setObject", UtilMap.toMap(new Object[]{"object", object}));
		 
	 }
	 
	 public static void setObjects(ActionContext actionContext){
		 Thing self = (Thing) actionContext.get("self");
		 Object object = actionContext.get("objects");
		 
		 ActionContext ac = (ActionContext) self.getData("actionContext");
		 ((ActionContainer) ac.get("actions")).doAction("setObjects", UtilMap.toMap(new Object[]{"objects", object}));		
	 }
	 
	 public static void dataTreeSelection(ActionContext actionContext){
		 Event event = (Event) actionContext.get("event");
		 Tree dataTree = (Tree) actionContext.get("dataTree");
		 Browser valueText = (Browser) actionContext.get("valueText");
		 World world = World.getInstance();
		 
		 if(event.widget == dataTree){
		     TreeItem item = dataTree.getSelection()[0];
		     Object value = item.getData();
		     if(value != null){
		         if(value instanceof Thing){
		        	 Thing thing = (Thing) value;
		             openValueUrl(valueText, "do?sc=xworker.ide.worldexplorer.swt.http.ThingDoc/@desc&thing=" + thing.getMetadata().getPath(), false);
		         }else if(value instanceof Throwable) {
		        	 Throwable t = (Throwable) value;
		        	 String stacks = ExceptionUtil.toString(t);
		        	 valueText.setText("<pre>" + stacks + "</pre>");
		         }else{
		             try{
		                 valueText.setText("<pre>" + value + "</pre>");
		             }catch(Throwable t){
		                 valueText.setText("<pre>" + value.getClass() + "</pre>");
		             }
		         }
		         
		         if(item.getItems().length == 0){
		             Action action = world.getAction("xworker.swt.xworker.ObjectViewer/@composite/@actions/@create1");
		             action.run(actionContext, UtilMap.toMap("treeItem", item, "value",  value, "context", new HashMap<Object, Object>()));
		             
		             //initDataTree(item, value, [:], actionContext, actions);
		         }
		     }else{
		         valueText.setText("");
		     }
		     return;
		 }
	 }
	 
	 public static void openValueUrl(Browser valueText, String url, boolean isOuterUrl){
		 World world = World.getInstance();
		 
	     if(isOuterUrl){
	         valueText.setUrl(url);
	     }else{
	         Thing globalCfg = world.getThing("_local.xworker.config.GlobalConfig");
	         valueText.setUrl(globalCfg.getString("webUrl") + url);
	     }
	 }
	 
	 public static void setObject2(ActionContext actionContext){
		 Tree dataTree = (Tree) actionContext.get("dataTree");
		 Object object = actionContext.get("object");
		 ActionContainer actions = (ActionContainer) actionContext.get("actions");
		 
		 //清除dataTree
		 for(TreeItem item : dataTree.getItems()){
		     item.dispose();
		 }

		 //增加子节点
		 String clsName = object != null ? object.getClass().getName() : "null";
		 String value = String.valueOf(object);
		 TreeItem treeItem = new TreeItem(dataTree, SWT.NONE);
		 treeItem.setText(new String[]{"object", value, clsName});
		 treeItem.setData(object);
		 treeItem.setData("name", "object");

		 
		 actions.doAction("initDataTree", UtilMap.toMap("treeItem", treeItem, "value", object, "context", new HashMap<Object, Object>()));
	 }
	 
	 @SuppressWarnings({ "unchecked", "rawtypes" })
	public static void initDataTree(ActionContext actionContext){
		 Object value = actionContext.get("value");
		 Map<Object, Object> context = (Map<Object, Object>) actionContext.get("context");
		 Object treeItem = actionContext.get("treeItem");
		 
		 try{
		     if(value == null || value.getClass() == null){
		         return;
		     }
		     
		     if(context.get(value) != null){
		         return;
		     }
		     context.put(value, value);
		     
		     List<Map<String, Object>> childs = new ArrayList<Map<String, Object>>();
		     Field[] fields = value.getClass().getFields();
		     for(Field field : fields){
		         Map<String, Object> child = new HashMap<String, Object>();
		         child.put("name", field.getName());
		         child.put("type", "field");
		         child.put("classType", field.getGenericType());
		         child.put("field", field);
		         child.put("value", field.get(value));
		         addChild(childs, child);
		     }
		     
		     Method[] methods = value.getClass().getMethods();
		     for(Method method : methods){
		    	 String methodName = method.getName();
		         if(methodName.startsWith("get") && method.getParameterTypes().length == 0){            
		             try{
		                 String name = methodName.substring(3,4).toLowerCase() + methodName.substring(4, methodName.length());
		              
		                 Map<String, Object> child = new HashMap<String, Object>();
		                 child.put("name", name);
		                 child.put("type", "method");
		                 child.put("method",  method);
		                 child.put("classType",  method.getReturnType());		                             
		                 child.put("value", method.invoke(value, new Object[]{}));                
		                 addChild(childs, child);            
		             }catch(Throwable e){
		                 //log.info("get mehtod error " + method.name);
		             }
		         }
		     }

		     if(value instanceof ActionContext){
		         /*def scopes = value.getScopes();
		         for(int i=scopes.size -1; i>=0; i--){
		             def child = [:];
		             child.name = "stack-" + i;
		             child.value = scopes.get(i);
		             child.classType = Bindings.class;
		             child.type = "field";
		             childs.add(child);
		             //addChild(childs, child); 
		         }*/
		     }else if(value instanceof ActionContainer){
		         Thing athing = ((ActionContainer) value).getThing();
		         if(athing != null){
		             for(Thing acThing : athing.getChilds()){
		                 Map<String, Object> child = new HashMap<String, Object>();
		                 child.put("name", acThing.getString("name"));
		                 child.put("value", acThing);
		                 child.put("classType", Thing.class);
		                 child.put("type", "field");
		                 addChild(childs, child);  
		             }
		         }
		     }else if(value instanceof Map){
		    	 Map vMap = (Map) value;
		         Map<String, Object> child = new HashMap<String, Object>();
		         child.put("name", "size");
		         child.put("value", vMap.size());
		         child.put("classType",  "int");
		         child.put("type", "field");
		         addChild(childs, child);  
		         
		         int index = 0;
		         for(Object key : vMap.keySet()){
		             child = new HashMap<String, Object>();
		             child.put("name", "" + key);
		             child.put("value", vMap.get(key));
		             child.put("type",  "field");
		             Object v = vMap.get(key);
		             child.put("classType", v != null ? v.getClass().getName() : "");
		             addChild(childs, child);
		             index++;
		             
		             //避免显示过多出问题
		             if(index > 1000){
		            	 break;
		             }
		         }
		     }else if(value instanceof List || value.getClass().getName().startsWith("[")){
		    	 List<Object> vlist = null;
		    	 if(value instanceof List){
		    		 vlist = (List<Object>) value;
		    	 }else{
		    		 vlist = new ArrayList<Object>();
		    		 for(Object obj : (Object[]) value){
		    			 vlist.add(obj);
		    		 }
		    	 }
		         Map<String, Object> child = new HashMap<String, Object>();
		         if(value instanceof List){
		             child.put("name", "size");
		             child.put("value",  vlist.size());
		         }else{
		             child.put("name", "length");
		             child.put("value", vlist.size());
		         }
		         child.put("classType", "int");
		         child.put("type", "field");
		         addChild(childs, child);  
		         
		         int index = 0;
		         for(Object v : vlist){
		             child = new HashMap<String, Object>();
		             if(index < 10){
		            	 child.put("name", "000" + index);
		             }else if(index < 100){
		            	 child.put("name", "00" + index);
		             }else if(index < 1000){
		            	 child.put("name", "0" + index);
		             }
		             index++;
		             child.put("value", v);
		             child.put("type", "field");
		             child.put("classType",v != null ? v.getClass() : "");
		             addChild(childs, child);  
		             
		             if(index >= 1000){
		            	 //最多显示1000个
		            	 break;
		             }
		         }
		     }
		     
		     for(Map<String, Object> child : childs){
		    	 String name = (String) child.get("name");
		         try{            
		             String v = null;		             
		             Object vobj = child.get("value");
		             Object c =  child.get("classType");
		             if((name == null || "".equals(name)) && vobj == null){
		            	 continue;
		             }
		             try{
		                 v = "" + String.valueOf(child.get("value"));//.toString();
		             }catch(Throwable e){
		                 v = String.valueOf(child.get("classType"));
		             }
		             TreeItem item = null;
		             if(treeItem instanceof Tree){
		            	 item = new TreeItem((Tree) treeItem, SWT.NONE);
		             }else{
		            	 item = new TreeItem((TreeItem) treeItem, SWT.NONE);
		             }
		             item.setText(new String[]{name, v, "" + c});
		             item.setData(vobj);
		             
		                         
		             if(vobj != null){      
		            	 //过滤类
		                 if(vobj instanceof Class){
		                     continue;
		                 }      
		                 //initDataTree(item, child.value, context);
		             }
		         }catch(Throwable e){
		             Executor.info(TAG, "init data item error " + name, e);
		         }
		     }
		 }catch(Throwable t){
		     t.printStackTrace();
		 }
	 }
	 
	 public static void  addChild(List<Map<String, Object>> childs, Map<String, Object> child){
	     for(int i=0; i<childs.size(); i++){
	         Map<String, Object> c = childs.get(i);
	         String name1 = (String) child.get("name");
	         String name2 = (String) c.get("name");
	         if(name1 == null){
	        	 break;
	         }
	         int cv = name1.compareTo(name2); 
	         if(cv < 0){
	             childs.add(i, child);
	             return;
	         }else if(cv == 0){
	             childs.set(i, child);
	             return;
	         }
	     }
	     
	     childs.add(child);
	 }
	 
	public static Object getSelectedObject(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		ActionContext ac = (ActionContext) self.getData("actionContext");
		Tree dataTree = (Tree) ac.get("dataTree");
		if(dataTree.getSelection() != null && dataTree.getSelection().length > 0) {
			return dataTree.getSelection()[0].getData();
		}else {
			return null;
		}
	}
	 
	 @SuppressWarnings("unchecked")
	public static void setObjects2(ActionContext actionContext){
		 Tree dataTree = (Tree) actionContext.get("dataTree");
		 Object objects = actionContext.get("objects");
		 //List<Map<String, Object>> objects = (ArrayList<Map<String, Object>>) actionContext.get("objects");
		 ActionContainer actions = (ActionContainer) actionContext.get("actions");
		 
		 //清除dataTree
		 if(dataTree == null || dataTree.isDisposed()){
			 return;
		 }
		 
		 dataTree.removeAll();
		 if(objects instanceof Map){
			 Map<String, Object> map = (Map<String, Object>) objects;
			 List<String> keys = new ArrayList<String>();
			 for(String key : map.keySet()){
				 if(key == null) {
					 continue;
				 }
				 keys.add(key);
			 }
			 Collections.sort(keys);
			 for(String key : keys){
				 Object object = map.get(key);
				 String value = String.valueOf(object);
				 String clsName = object != null ? object.getClass().getName() : "null";
				 TreeItem  treeItem = new TreeItem(dataTree, SWT.NONE);
			     treeItem.setText(new String[]{key, value, clsName});
			     treeItem.setData(object);
			     treeItem.setData("name", key);
			     
			     actions.doAction("initDataTree", UtilMap.toMap("treeItem", treeItem, "value",  object, "context", new HashMap<Object, Object>()));
			 }
		 }else{
			 List<Map<String, Object>> objs = (List<Map<String, Object>>) objects;
			 //增加子节点
			 for(Map<String, Object> data : objs){
			     Object object = data.get("object");
			     String clsName = object != null ? object.getClass().getName() : "null";
			     String value = String.valueOf(object);
			     String name = (String) data.get("name");
			     
			     TreeItem  treeItem = new TreeItem(dataTree, SWT.NONE);
			     treeItem.setText(new String[]{name, value, clsName});
			     treeItem.setData(object);
			     treeItem.setData("name", name);
			     
			     actions.doAction("initDataTree", UtilMap.toMap("treeItem", treeItem, "value",  object, "context", new HashMap<Object, Object>()));
			 }
		 }
	 }
	 
	 public static Composite getControl(ActionContext actionContext) {
		 Thing self = (Thing) actionContext.get("self");
		 ActionContext ac = (ActionContext) self.getData("actionContext");
		 
		 return ac.getObject("composite");
	 }
	 
	 public static void addObject(ActionContext actionContext) {
		 Thing self = (Thing) actionContext.get("self");
		 ActionContext ac = (ActionContext) self.getData("actionContext");
		 ((ActionContainer) ac.get("actions")).doAction("addObject", ac,
				 "object", actionContext.get("object"), "name", actionContext.get("name"));
	 }
	 
	 public static void addObjectAction(ActionContext actionContext) {
		 Object object = actionContext.getObject("object");
		 Tree dataTree = (Tree) actionContext.get("dataTree");
		 ActionContainer actions = (ActionContainer) actionContext.get("actions");
		 String name = actionContext.getObject("name");
		 
		 String clsName = object != null ? object.getClass().getName() : "null";
	     String value = String.valueOf(object);
	     if(name == null) {
	    	 name = "object";
	     }
	     
	     TreeItem  treeItem = new TreeItem(dataTree, SWT.NONE);
	     treeItem.setText(new String[]{name, value, clsName});
	     treeItem.setData(object);
	     treeItem.setData("name", name);
	     
	     actions.doAction("initDataTree", UtilMap.toMap("treeItem", treeItem, "value",  object, "context", new HashMap<Object, Object>()));
	 }
	 
	 public static void removeAll(ActionContext actionContext) {
		 Thing self = (Thing) actionContext.get("self");
		 ActionContext ac = (ActionContext) self.getData("actionContext");
		 Tree dataTree = (Tree) ac.get("dataTree");
		 dataTree.removeAll();
	 }
}