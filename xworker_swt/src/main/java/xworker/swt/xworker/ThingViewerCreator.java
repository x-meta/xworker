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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.ThingMetadata;
import org.xmeta.World;
import org.xmeta.codes.XmlCoder;
import org.xmeta.util.UtilMap;

import xworker.lang.executor.Executor;
import xworker.swt.design.Designer;
import xworker.swt.form.ThingDescriptorForm;
import xworker.swt.util.XWorkerTreeUtil;
import xworker.swt.xwidgets.CodeViewer;
import xworker.util.UtilData;

public class ThingViewerCreator {
    public static Object create(ActionContext actionContext){
    	World world = World.getInstance();
    	Thing self = (Thing) actionContext.get("self");
    	
		ActionContext ac = new ActionContext();
		ac.put("parent", actionContext.get("parent"));
		ac.put("viewerThing", self);
		ac.put("parentContext", actionContext);
		
		//创建面板
		Thing compositeThing = world.getThing("xworker.swt.xworker.ThingViewer/@composite");
		Composite composite = null;
		Designer.pushCreator(self);
		try{
			composite = (Composite) compositeThing.doAction("create", ac);
		}finally{
			Designer.popCreator();
		}
		
		try{
		    //创建子事物，通常是布局数据
		    Bindings bindings = ac.push(null);
		    bindings.put("parent", composite);
		    for(Thing child : self.getAllChilds()){
		        child.doAction("create", ac);
		    }
		}finally{
		    ac.pop();
		}
		
		//创建事物定义
		Thing form = new Thing("xworker.swt.xworker.ThingViewer");
		form.setData("formThing", self);
		form.setData("parent", composite);
		form.set("extends", self.getMetadata().getPath());
		form.setData("actionContext", ac);
		ac.getScope(0).put("self", form);
		form.put("H_SCROLL", "true");
		form.put("V_SCROLL", "true");
		
		if(self.getString("thing") != null && !"".equals(self.getString("thing"))){
		    Thing thing = world.getThing(self.getString("thing"));
		    form.doAction("setThing", ac, UtilMap.toParams(new Object[]{"thing", thing}));
		}
		
		actionContext.getScope(0).put(self.getString("name"), form);
		Designer.attachCreator(composite, self.getMetadata().getPath(), actionContext);
		return composite;        
	}

    public static void setThing(ActionContext actionContext){
    	Thing self = (Thing) actionContext.get("self");
    	
		ActionContext ac = (ActionContext) self.getData("actionContext");
		//初始化概要树
		Tree outlineTree = (Tree) ac.get("outlineTree");
		for(TreeItem item : outlineTree.getItems()){
		    item.dispose();
		}
		
		if(actionContext.get("thing") == null){
		    for(Control child : ((Composite) ac.get("thingComposite")).getChildren()){
		        child.dispose();
		    }
		    return;
		}
		
		Thing thing = (Thing) actionContext.get("thing");
		if(UtilData.isTrue(actionContext.getObject("editorThingUseRootThing"))) {
			initOutlineItem(thing.getRoot(), outlineTree, thing, outlineTree, ac.get("outlineTreeSelection"), actionContext);
		}else {
			initOutlineItem(thing, outlineTree, thing, outlineTree, ac.get("outlineTreeSelection"), actionContext);
		}
	}
    
    public static void initOutlineItem(Thing thing, Object parentItem, Thing rootThing, Tree outlineTree, Object outlineTreeSelection, ActionContext actionContext){
	    TreeItem treeItem = null;
	    if(parentItem instanceof Tree){
	    	treeItem = new TreeItem((Tree) parentItem, SWT.NONE);
	    }else if(parentItem instanceof TreeItem){
	    	treeItem = new TreeItem((TreeItem) parentItem, SWT.NONE);
	    }
	    treeItem.setData(thing);
	    ThingMetadata metadata = thing.getMetadata();
	    treeItem.setText(metadata.getLabel() + " (" + thing.getThingName() + ")");
	    XWorkerTreeUtil.initItem(treeItem, thing, actionContext);
	    
	    for(Thing child : thing.getChilds()){
	        initOutlineItem(child, treeItem, rootThing, outlineTree, outlineTreeSelection, actionContext);
	    }
	    treeItem.setExpanded(true);
	    
	    if(thing == rootThing){
	        outlineTree.select(treeItem);
	        ((Listener) outlineTreeSelection).handleEvent(null);
	    }
	}        
    
    public static void selection(ActionContext actionContext){
    	Tree outlineTree = (Tree) actionContext.get("outlineTree");
    	Composite thingComposite = (Composite) actionContext.get("thingComposite");
    	Thing self = (Thing) actionContext.get("self");
    	CodeViewer xmlText = actionContext.getObject("xmlText");
    	
    	Event event = actionContext.getObject("event");
    	Thing thing = null;
    	if(event != null){
    		thing = (Thing) event.item.getData();
    	}else{
    		TreeItem[] items = outlineTree.getSelection();
    		if(items != null && items.length > 0){
    			thing = (Thing) items[0].getData();
    		}else{
    			return;
    		}
    	} 
    	try{
    	    for(Control child : thingComposite.getChildren()){
    	        child.dispose();
    	    }
    	    
    	    Bindings bindings = actionContext.push(null);
    	    bindings.put("thing", thing);
    	    bindings.put("thingAttributes", thing.getAttributes());
    	    bindings.put("parent", thingComposite);
    	    self.put("descriptorPath", thing.getDescriptor().getMetadata().getPath());
    	    
    	    Composite formComposite = ThingDescriptorForm.createForm(actionContext, 1);
    	    ActionContext newContext = (ActionContext) formComposite.getData("actionContext");
    	    ((Thing) newContext.get("model")).doAction("init", newContext);
    	    ((Thing) newContext.get("model")).doAction("setValue", newContext);
    	    
    	    thingComposite.layout();
    	    
    	    //xml
    	    String xml = XmlCoder.encodeToString(thing);
    	     
    	    xmlText.setCode("xml", "xml", xml);
    	    //Executor.info(ThingViewerCreator.class.getName(), xml);
    	    
    	    //触发selection事件
    	    Thing viewerThing = actionContext.getObject("viewerThing");
    	    viewerThing.doAction("selection", (ActionContext) actionContext.get("parentContext"), "thing", thing);
    	}finally{
    	    actionContext.pop();
    	}
    }
    
    public static void editorSetThing(ActionContext actionContext){
    	Thing editor = (Thing) actionContext.get("editor");
    	editor.doAction("setThing", actionContext);
    }
}