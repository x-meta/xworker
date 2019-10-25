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
package xworker.app.model.tree;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

public class TreeModelSwtActionCreator {
    @SuppressWarnings("unchecked")
	public static void create(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	World world = World.getInstance();
        
        //创建树模型实例，树模型实例也是事物，结构为本事物，其中属性modelThing引用本事物
        Thing model = null;
        String treeModelRef = self.getString("treeModelRef");
        if(treeModelRef != null && !"".equals(treeModelRef)){
            model = (Thing) actionContext.get(treeModelRef);
            if(model == null){
                Thing modelThing = world.getThing(treeModelRef);
                if(modelThing != null){
                    model = new Thing(treeModelRef);
                    model.put("descriptors", "xworker.app.model.tree.TreeModelInsAction");
                    model.put("extends", "xworker.app.model.tree.TreeModelInsAction");
                    model.put("modelThing", modelThing);
                    model.put("listeners", new ArrayList<Thing>());
                }
            }
        }
        
        if(model == null){
            model = new Thing();
            model.put("descriptors", "xworker.app.model.tree.TreeModelInsAction");
            model.put("extends", "xworker.app.model.tree.TreeModelInsAction");
            model.put("modelThing", self);
            model.put("listeners",  new ArrayList<Thing>());
        }
        
        actionContext.getScope(0).put(self.getString("name"), model);
        
        //create方法一般在swt里使用，看看是否要绑定到父节点
        if(self.getBoolean("bindToParent")){
            Object parent = actionContext.get("parent");
            attach(parent, model, actionContext);
            
            String parentControls = self.getStringBlankAsNull("parentControls");
            if(parentControls != null) {
            	for(String p : parentControls.split("[,]")) {
            		Object control = actionContext.get(p);
            		attach(control, model, actionContext);
            	}
            }
        }
    }
    
    @SuppressWarnings("unchecked")
	public static void attach(Object parent, Thing model, ActionContext actionContext) {
    	if(parent == null) {
    		return;
    	}
    	
    	if(parent instanceof Tree || parent instanceof TreeItem){
            //绑定到Tree或者TreeItem，通过注册监听的方式绑定，监听的init完成创建节点等等事务
            Thing listener = new Thing("xworker.app.model.tree.swt.TreeModelTreeListener");
            ((List<Thing>) model.get("listeners")).add(listener);
            listener.put("treeModel", model);
            listener.doAction("init", actionContext, UtilMap.toMap(new Object[]{"treeModel",model, "parent",parent}));
            
            model.put("parent", parent);
        }
    }

    public static void treeModelsCreate(ActionContext actionContext) {
    	Thing self = actionContext.getObject("self");
    	actionContext.peek().put("parent", null);
    	for(Thing child :self.getChilds()) {
    		child.doAction("create", actionContext);
    	}
    }
}