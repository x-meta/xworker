package xworker.ide.worldexplorer.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Category;
import org.xmeta.Index;
import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.lang.system.message.MessageCenter;
import xworker.service.ServiceManager;
import xworker.swt.ActionContainer;
import xworker.swt.events.SwtListener;
import xworker.util.XWorkerUtils;

public class ProjectTreeActions {
	static Map<Index, Index> expandedItemCahces = new HashMap<Index, Index>();
	
	static Index currentSelectedIndex = null;
	
	public static void setSelectedIndex(Index index){
		currentSelectedIndex = index;
	}
	
	public static void projectTreeExpand(ActionContext actionContext){
		Event event = (Event) actionContext.get("event");
		TreeItem item = (TreeItem) event.item;
		Index index = (Index) item.getData();
		expandedItemCahces.put(index, index);
		
		MessageCenter.publish("/xworker/ide/projecttree/treeexpand", item, actionContext);
	}
	
	public static void projectTreeCollapse(ActionContext actionContext){
		Event event = (Event) actionContext.get("event");
		TreeItem item = (TreeItem) event.item;
		Index index = (Index) item.getData();
		expandedItemCahces.remove(index);
		
		MessageCenter.publish("xworker/ide/projecttree/treecollapse", item, actionContext);
	}
	
	public static void refreshProjectTree(ActionContext actionContext){
		Tree projectTree  = (Tree) actionContext.get("projectTree");	
		Index index = Index.getInstance();
		
		for(Index child : index.getChilds()){
			child.refresh();
		}
		index.refresh();
		
		for(TreeItem item : projectTree.getItems()){
			item.dispose();
		}
		
		for(Index childIndex : index.getChilds()){
			Bindings bindings = actionContext.push();
			bindings.put("item", projectTree);
			bindings.put("index", childIndex);
			TreeItem treeItem = initProjectTreeItem(actionContext);
			if(currentSelectedIndex == childIndex){
				projectTree.setSelection(treeItem);
			}
		}
		
		if(projectTree.getSelection().length > 0){
			SwtListener listener = (SwtListener) actionContext.get("projectTreeSelection");
			listener.handleEvent(null);
		}
		
		MessageCenter.publish("xworker/ide/projecttree/refresh", projectTree, actionContext);
	}
	
	public static TreeItem initProjectTreeItem(ActionContext actionContext){
		Object item = actionContext.get("item");
		Index index = (Index) actionContext.get("index");
		Tree projectTree  = (Tree) actionContext.get("projectTree");	
		
		TreeItem treeItem = null;
		if(item instanceof Tree){
			treeItem = new TreeItem((Tree) item, SWT.NONE);
		}else{
			treeItem = new TreeItem((TreeItem) item, SWT.NONE);
		}
		
		initTreeItem(treeItem, index, actionContext);
		
		if(expandedItemCahces.get(index) != null){			
			for(Index childIndex : index.getChilds()){
				if("thing".equals(childIndex.getType())){
					continue;
				}
				
				Bindings bindings = actionContext.push();
				bindings.put("item", treeItem);
				bindings.put("index", childIndex);
				TreeItem childItem = initProjectTreeItem(actionContext);
				if(currentSelectedIndex == childIndex){
					projectTree.setSelection(childItem);
				}
			}
			
			treeItem.setExpanded(true);
		}
		
		MessageCenter.publish("xworker/ide/projecttree/inititem", treeItem, actionContext);
		return treeItem;
	}
	
	private static void initTreeItem(TreeItem treeItem, Index index, ActionContext actionContext){
		treeItem.setText(index.getLabel());
		treeItem.setData(index);
		String type = index.getType();
		if("project".equals(type)){
		    treeItem.setImage((Image) actionContext.get("project1Image"));
		}
		if("thingManager".equals(type)){
		        treeItem.setImage((Image) actionContext.get("factoryImage"));
		}else if("category".equals(type)){
		        treeItem.setImage((Image) actionContext.get("packageImage"));
		}else if("thing".equals(type)){
		        treeItem.setImage((Image) actionContext.get("dataObjectImage"));
		}else if("workingSet".equals(type)){
		        treeItem.setImage((Image) actionContext.get("project1Image"));
		}

	}
	
	public static void expandProjectTree(ActionContext actionContext){
		Tree projectTree  = (Tree) actionContext.get("projectTree");
		TreeItem item = projectTree.getSelection()[0];
		Index data = (Index) item.getData();
		ActionContainer actions = (ActionContainer) actionContext.get("actions");

		if(item.getItems().length == 0){
		    //如果当前节点没有子节点，那么打开子节点
		    //println data;
		    if(data == null){
		        //节点已经不存在
		        item.dispose();
		        return;
		    }
		    
		    data.refresh();
		    for(Index childIndex : data.getChilds()){
		        //项目导航不显示事物节点
		        if("thing".equals(childIndex.getType())) continue;
		        
		        actions.doAction("initProjectTreeItem", actionContext,  
		        		UtilMap.toMap(new Object[]{"item", item, "index", childIndex}));
		    }
		}

		//打开本节点关闭其他同级节点
		TreeItem parentItem = item.getParentItem();
		if(parentItem != null){
		    for(TreeItem titem : parentItem.getItems()){
		         if(titem != item && titem.getExpanded()){
		             //titem.setExpanded(false);
		         }
		    }
		}
		item.setExpanded(true);
		
		expandedItemCahces.put(data, data);
	}
	
	public static void init(final ActionContext actionContext) {
		Index index = Index.getInstance();
		org.xmeta.util.ActionContainer actions = actionContext.getObject("actions");
	    org.eclipse.swt.widgets.Tree projectTree = actionContext.getObject("projectTree");
	    
		projectTree.setData(index);
		for(Index subIndex : index.getChilds()){
		    actions.doAction("initProjectTreeItem", actionContext, 
		        "item", projectTree, "index", subIndex);
		    //TreeItem projectItem = new TreeItem(projectTree, SWT.NONE);
		    //projectItem.setText(UtilString.getString("res:res.w_exp:" + index.getLabel() + ":" + index.getLabel(), actionContext));
		    //projectItem.setData(index);
		    //projectItem.setImage(projectImage);
		}
		
		ServiceManager.regist(new ProjectTreeService() {			
			@Override
			public void locate(Thing thing) {
				actionContext.push().put("object", thing);
				try {
					ProjectTreeActions.locate(actionContext);
				}finally {
					actionContext.pop();
				}
			}
			
		}, ProjectTreeService.class);
	}
	
	public static void locate(ActionContext actionContext) {
		Object object = actionContext.get("object");
		if(object == null) {
			return;
		}
		
		org.eclipse.swt.widgets.Tree projectTree = actionContext.getObject("projectTree");
		if(object instanceof Thing) {
			Thing thing = (Thing) object;
			Index index = findIndex(thing);
			List<Index> indexs = new ArrayList<Index>();
			while(index.getParent() != null) {
				indexs.add(0, index);
				index = index.getParent();
			}
			
			TreeItem item = null;
			for(Index idx : indexs) {
				if(item == null) {
					for(TreeItem itm : projectTree.getItems()) {
						if(itm.getData() == idx) {
							item = itm;
							break;
						}
					}
				} else {
					if(item.getItemCount() == 0) {
						for(Index childIndex : idx.getParent().getChilds()){
							Bindings bindings = actionContext.push();
							bindings.put("item", item);
							bindings.put("index", childIndex);
							initProjectTreeItem(actionContext);
						}
					}
					
					for(TreeItem itm : item.getItems()) {
						if(itm.getData() == idx) {
							item = itm;
							break;
						}
					}
				}
			}
			
			if(item != null) {
				projectTree.setSelection(item);
				projectTree.showSelection();
			}
		}
	}
	
	private static Index findIndex(Thing thing) {
		Index index = findIndex(thing.getMetadata().getThingManager());
		if(index == null) {
			return null;
		}
		
		String path = thing.getMetadata().getPath();
		int offset = path.lastIndexOf(".");
		if(offset != -1) {
			path = path.substring(0, offset);
		}
		
		String paths[] = path.split("[.]");		
		for(String name : paths) {
			for(Index child : index.getChilds()) {
				if(child.getType().equals(Index.TYPE_CATEGORY) && child.getName().equals(name)) {
					index = child;
					break;
				}
			}
		}
		
		return index;
	}
	
	private static Index findIndex(ThingManager thingManager) {
		Index index = Index.getInstance();
		for(Index child : index.getChilds()) {
			if(child.getIndexObject() == thingManager) {
				return child;
			}
			
			for(Index cchild : child.getChilds()) {
				if(cchild.getIndexObject() == thingManager) {
					return cchild;
				}
			}
		}
		
		return null;
	}
	
    public static void projectTreeSelection(ActionContext actionContext){
        //xworker.swt.app.views.ProjectTree/@tree/@Listeners/@projectTreeSelection/@GroovyAction
    	Tree projectTree = actionContext.getObject("projectTree");
    	ActionContainer actions = actionContext.getObject("actions");
    	Object projectTreeSelection = actionContext.getObject("projectTreeSelection");
    	Action openEditor = actionContext.getObject("openEditor");
    	
        setSelectedIndex((Index) projectTree.getSelection()[0].getData());
        
        actions.doAction("expandProjectTreeItem");
        
        //通知辅助者触发事件
        //Assistor.projectTreeSelected(projectTree.getSelection()[0], projectTree.getSelection()[0].getData());
        
        TreeItem item = projectTree.getSelection()[0];
        Map<String, Object> params = UtilMap.toMap("index", item.getData(), 
             "treeItem", item,
             "projectTree", item.getParent(), 
             "projectTreeSelection", projectTreeSelection,
             "explorerActions", XWorkerUtils.getIdeActionContainer());
        openEditor.run(actionContext, "params", params);
        MessageCenter.publish("/xworker/ide/projecttree/selection", params, actionContext) ;
    }
    
    public static Object ProjectTreeMenuDetect(ActionContext actionContext){
        //xworker.swt.app.views.ProjectTree/@tree/@Listeners/@projectTreeMenudetect/@ProjectTreeMenuDetect
    	Tree projectTree = actionContext.getObject("projectTree");
    	//ActionContainer actions = actionContext.getObject("actions");
    	Menu projectTreeMenu = actionContext.getObject("projectTreeMenu");
        
        for(MenuItem item : projectTreeMenu.getItems()){
            item.dispose();
        }
        
        //设置弹出菜单的变量
        TreeItem treeItem = projectTree.getSelection()[0];
        
        Index data = (Index) treeItem.getData();
        //log.info(data.type);
        String menuPath = "";
        if("world".equals(data.getType()) || "workingSet".equals(data.getType())) {
            menuPath = "xworker.ide.worldexplorer.swt.dataExplorerParts.ProjectTreeMenu/@shell/@projectComposite/@projectMenu";
        } else if("plugins".equals(data.getType()) || "projects".equals(data.getType()) || "virtuals".equals(data.getType())) {           
            menuPath = "xworker.ide.worldexplorer.swt.dataExplorerParts.ProjectTreeMenu/@shell/@dataCenterComposite/@dataCenterMenu";
        } else if("thingManager".equals(data.getType())) {           
            menuPath = "xworker.ide.worldexplorer.swt.dataExplorerParts.ProjectTreeMenu/@shell/@cactoryComposite/@facotyMenu";
        } else if("category".equals(data.getType())) {           
        	menuPath = "xworker.ide.worldexplorer.swt.dataExplorerParts.ProjectTreeMenu/@shell/@packageComposite/@packageMenu";
        } else if("thing".equals(data.getType())) {           
        	menuPath = "xworker.ide.worldexplorer.swt.dataExplorerParts.ProjectTreeMenu/@shell/@dataObjectComposite/@dataObjectMenuItem";
        } else {
        	menuPath = "xworker.ide.worldexplorer.swt.dataExplorerParts.ProjectTreeMenu/@shell/@projectComposite/@projectMenu";
        }
        
        ActionContext newContext = new ActionContext();
        newContext.put("parent", projectTreeMenu);
        newContext.put("explorerActions", XWorkerUtils.getIdeActionContainer());
        newContext.put("explorerActionContext", XWorkerUtils.getIdeActionContainer().getActionContext());
        newContext.put("treeItem", treeItem);
        newContext.put("projectTree", projectTree);
        
        Thing menuObj = World.getInstance().getThing(menuPath);
        if(menuObj != null){
            for(Thing child : menuObj.getChilds()){
                if("versionMenu".equals(child.getMetadata().getName())){
                    continue;
                }
                child.doAction("create", newContext);
            }
        }
        
        projectTreeMenu.setData("menuContext", newContext);
        MessageCenter.publish("/xworker/ide/projecttree/menudetect", newContext, actionContext);
        return newContext;
    }
    
    public static void prjTreeDragStrat(ActionContext actionContext){
        //xworker.swt.app.views.ProjectTree/@tree/@projectTreeDragSource/@prjTreeDragStrat/@run
    	Tree projectTree = actionContext.getObject("projectTree");
    	//ActionContainer actions = actionContext.getObject("actions");
    	Event event = actionContext.getObject("event");
        
        TreeItem[] selection = projectTree.getSelection();
        if (selection.length > 0) {
            TreeItem item = selection[0];
            if(!Index.TYPE_CATEGORY.equals(((Index) item.getData()).getType())){
                event.doit = false;
            }else{
                event.doit = true;
            }
        } else {
            event.doit = false;
        }
    }
    
    public static void prjTreeSetData(ActionContext actionContext){
        //xworker.swt.app.views.ProjectTree/@tree/@projectTreeDragSource/@prjTreeSetData/@run
    	Tree projectTree = actionContext.getObject("projectTree");
    	TypedEvent event = actionContext.getObject("event");
    	
        TreeItem item = projectTree.getSelection()[0];
        Index index = (Index) item.getData();
        event.data = ((Category) index.getIndexObject()).getThingManager().getName() + ":" + index.getPath();
    }
    
    public static void prjTreeDragOver(ActionContext actionContext){
        //xworker.swt.app.views.ProjectTree/@tree/@projectTreeDrogTarget/@prjTreeDragOver/@run
    	//Tree projectTree = actionContext.getObject("projectTree");
    	DropTargetEvent event = actionContext.getObject("event");
        
        event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL | DND.FEEDBACK_SELECT;;
        if(event.item != null){
            //如果没有展开的节点，展开
            //actions.doAction("expandProjectTreeItem", ["itemForExpand":event.item]);
            
        	String type = ((Index) event.item.getData()).getType();
            if(Index.TYPE_CATEGORY.equals(type) || Index.TYPE_THINGMANAGER.equals(type)){
                event.detail = DND.DROP_MOVE;
            }else{
                event.detail = DND.DROP_NONE;
            }
        }
    }
    
    public static void prjTreeDrop(ActionContext actionContext){
        //xworker.swt.app.views.ProjectTree/@tree/@projectTreeDrogTarget/@prjTreeDrop/@run
    	DropTargetEvent event = actionContext.getObject("event");
    	Tree projectTree = actionContext.getObject("projectTree");
    	ActionContainer actions = actionContext.getObject("actions");
        
        if(event.item != null){
            String srcPath = (String) event.data;
            String srcName = srcPath;
            if(srcPath.lastIndexOf(".") != -1){
                srcName = srcPath.substring(srcPath.lastIndexOf(".") + 1);
            }else{
                srcName = srcPath.substring(srcPath.lastIndexOf(":") + 1);
            }
            Object targetIndexObject = ((Index) event.item.getData()).getIndexObject();
            Object targetCategory = targetIndexObject;
            if(targetIndexObject instanceof ThingManager){
                targetCategory = ((ThingManager) targetIndexObject).getCategory(null);
            }
            
            String targPath = ((Category) targetCategory).getThingManager().getName() + ":" ;
            String name = ((Category) targetCategory).getName();
            if(name == null || "".equals(name)){
                targPath = targPath + srcName;
            }else{
                targPath = targPath + name + "." + srcName;
            }
            
            Shell shell = projectTree.getShell();
            ActionContext ac = new ActionContext();
            ac.put("parent", shell);
            ac.put("srcPath", srcPath);
            ac.put("targPath", targPath);
            ac.put("sourceTreeItem", projectTree.getSelection()[0]);
            
            Thing shellThing = World.getInstance().getThing("xworker.ide.worldexplorer.swt.dialogs.ThingRefactorDialog/@shell");
            shell = shellThing.doAction("create", ac);
            shell.setVisible(true);
            
            Display display = shell.getDisplay();
            while (!shell.isDisposed()) {
                if (!display.readAndDispatch()) display.sleep();
            }
            
            World world = World.getInstance();
            if(ac.get("executed") == "true"){
                //如果原始目录已移除，那么删除项目树中间的节点        
                if(world.get(srcPath) == null && srcPath == ((Index) projectTree.getSelection()[0].getData()).getPath()){
                    projectTree.getSelection()[0].dispose();
                }
                projectTree.setSelection((TreeItem) event.item);
                
                actions.doAction("refreshProjectTree");
            }
        }
    }
}
