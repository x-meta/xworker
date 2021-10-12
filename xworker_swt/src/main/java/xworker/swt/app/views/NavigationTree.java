package xworker.swt.app.views;

import java.util.*;

import org.eclipse.swt.widgets.Event;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.app.model.tree.TreeModel;
import xworker.app.model.tree.TreeModelItem;
import xworker.app.model.tree.TreeModelUtils;

public class NavigationTree {
    @SuppressWarnings("unchecked")
	public static void onSelection(ActionContext actionContext){
	    //xworker.swt.app.views.NavigationTree/@tree/@onSelection/@onSelection
    	Event event = actionContext.getObject("event");
    	TreeModelItem item = (TreeModelItem) event.item.getData();
    	if(item != null){
			((Thing) item.getSource()).doAction("run", actionContext);
		}
	}
	
	public static void mavTreeReloadItemSelection(ActionContext actionContext){
	    //xworker.swt.app.views.NavigationTree/@tree/@mavTreeMenu/@mavTreeReloadItem/@listeners/@Listener/@mavTreeReloadItemSelection
		TreeModel treeModel = actionContext.getObject("treeModel");
		treeModel.reload(null);
		/*
		ActionContext parentContext = actionContext.getObject("parentContext");
		Thing workbenchThing = parentContext.getObject("workbenchThing");
	    Thing treeModelThing =  workbenchThing.getThing("AppTreeModel@0");
	    if(treeModelThing != null){
	        String name = treeModelThing.getMetadata().getName();
	        Thing treeModel = actionContext.getObject(name);
	        //println treeModel;
	        if(treeModel != null){
	            treeModel.doAction("reload", actionContext);
	        }
	    }*/
	}
	
	public static void  init(ActionContext actionContext){
	    //xworker.swt.app.views.NavigationTree/@tree/@init
	    //编辑器容器
		ActionContext parentContext = actionContext.getObject("parentContext");
	    actionContext.g().put("editorContainer", parentContext.get("editorContainer"));
	    
	    //设置导航树模型
	    Thing workbenchThing = parentContext.getObject("workbenchThing");
	    Thing appTreeModel  = workbenchThing.doAction("getAppTreeModel", parentContext);
	    if(appTreeModel != null) {			
	        appTreeModel.doAction("create", actionContext);
	    }
	}

	public static TreeModelItem getRoot(ActionContext actionContext){
		TreeModel treeModel = actionContext.getObject("treeModel");

		ActionContext parentContext = actionContext.getObject("parentContext");
		Thing workbenchThing = parentContext.getObject("workbenchThing");
		Thing appTreeModel  = workbenchThing.doAction("getAppTreeModel", parentContext);

		if(appTreeModel != null) {
			return TreeModelUtils.toItem(treeModel, null, appTreeModel);
		}else{
			return null;
		}
    }

    public static List<TreeModelItem> getChilds(ActionContext actionContext){
		ActionContext parentContext = actionContext.getObject("parentContext");
		Thing workbenchThing = parentContext.getObject("workbenchThing");
		Thing appTreeModel  = workbenchThing.doAction("getAppTreeModel", parentContext);
		if(appTreeModel != null) {
			return appTreeModel.doAction("getChilds", actionContext);
		}

		return Collections.emptyList();
	}
}
