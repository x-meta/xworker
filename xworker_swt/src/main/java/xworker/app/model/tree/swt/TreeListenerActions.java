package xworker.app.model.tree.swt;

import java.util.List;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;

public class TreeListenerActions {
	public static void TreeDisposeListener(ActionContext actionContext){
		Event event = (Event) actionContext.get("event");
		
		Thing treeModel = (Thing) event.widget.getData("_treeModel");
		if(treeModel != null){
		    treeModel.set("" + event.widget.hashCode(), null);
		}
	}
	
	public static void TreeItemDisposeListener(ActionContext actionContext){
		Event event = (Event) actionContext.get("event");
		
		if(event == null || event.item == null){
		    return;
		}

		Thing treeModel = (Thing) event.item.getData("_treeModel");
		if(treeModel != null){
		    treeModel.set("" + event.item.hashCode(), null);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void TreeModelSelection(ActionContext actionContext) throws OgnlException{
		Event event = (Event) actionContext.get("event");
		TreeItem item = (TreeItem) event.item;
		Object treeNode = item.getData("_treeNode");
		Object treeNodeId = item.getData("_treeNodeId");
		//log.info("treeNode=" + treeNode + ",treeNodeId=" + treeNodeId);
		if(treeNode != null){
		    //是否动态刷新
			Object leaf = OgnlUtil.getValue("leaf", treeNode);
		    if(!UtilData.isTrue(leaf)){
		        //log.info("refresh tree items");
		        if(item.getItems().length == 0){
		            Thing rootModel = (Thing) item.getData("_treeModel");
		            if(rootModel == null){
		                return;
		            }
		            
		            List<Object> childs = (List<Object>) rootModel.doAction("getChilds", actionContext, UtilMap.toMap("id", treeNodeId, "parentNode", treeNode, "parentTreeItem", item));
		            //log.info("childs=" + childs);

		            if(childs != null && childs.size() > 0){
		               // rootModel.doAction("fireEvent", actionContext, 
		                //        ["eventName":"onNodeInserted", "treeNodes":childs, "node":null, "nodes": childs, "parentId":treeNodeId, "index": -1]);               
		                for(Thing listener : (List<Thing>) rootModel.get("listeners")){
		                    //log.info("tree listener=" + listener);
		                    listener.doAction("onNodeInserted", actionContext, UtilMap.toMap("node", null, "nodes", childs, "parentId",treeNodeId, "index", -1, "parentTreeItem", item));
		                }
		                
		                //这样的不会触发treeListener事件，需要手工保存打开状态
		                TreeModelTreeListenerCreator.setExpandedCache((Thing) item.getData("_treeModel"), treeNodeId);
		            }
		        }
		    }
		    
		    if(UtilData.isTrue(OgnlUtil.getValue("singleClickExpand", treeNode))){
		        if(item.getExpanded() == false){
		            item.setExpanded(true);
		        }
		    }
		}
	}
	
}
