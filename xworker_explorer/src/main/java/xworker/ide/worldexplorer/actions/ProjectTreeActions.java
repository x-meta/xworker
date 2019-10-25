package xworker.ide.worldexplorer.actions;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Index;
import org.xmeta.util.UtilMap;

import xworker.swt.ActionContainer;
import xworker.swt.events.SwtListener;

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
	}
	
	public static void projectTreeCollapse(ActionContext actionContext){
		Event event = (Event) actionContext.get("event");
		TreeItem item = (TreeItem) event.item;
		Index index = (Index) item.getData();
		expandedItemCahces.remove(index);
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
}
