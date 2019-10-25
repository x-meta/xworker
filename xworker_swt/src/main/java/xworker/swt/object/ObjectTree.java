package xworker.swt.object;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;
import org.xmeta.util.UtilMap;

import ognl.OgnlException;
import xworker.swt.ActionContainer;
import xworker.swt.util.ResourceManager;
import xworker.util.UtilData;

public class ObjectTree {
	public static Object create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//创建树对象
		ActionContext ac = new ActionContext();
		ac.put("parent", actionContext.get("parent"));
		ac.put("thing", self);
		
		Thing treeThing = World.getInstance().getThing("xworker.swt.object.ObjectTree/@tree");
		Tree tree = (Tree) treeThing.doAction("create", ac);
		ActionContainer actions = (ActionContainer) ac.get("actions");
		
		//创建子节点
		actionContext.peek().put("parent", tree);
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
		
		//如果有对象，那么显示
		Object object = self.doAction("getObject", actionContext);
		if(object != null){
			actions.doAction("setObject", actionContext, UtilMap.toMap("object", object, "context", actionContext));
		}
		
		tree.setData(object);
		tree.setData("thing", self);
		
		actionContext.g().put(self.getMetadata().getName(), actions);
		
		return tree;
	}
	
	public static void setObject(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Tree tree = (Tree) actionContext.get("tree");
		Object object = actionContext.get("object");
		
		//先移除已有的所有节点
		tree.removeAll();
		ActionContext context = (ActionContext) actionContext.get("context");
		context.peek().put("_object", object);
		context.peek().put("_rootObject", object);
		context.peek().put("treeItem", tree);
		
		actionContext.peek().put("_object", object);
		actionContext.peek().put("_rootObject", object);
		actionContext.peek().put("treeItem", tree);
		actionContext.peek().put("parent", tree);
		for(Thing node : self.getChilds("Node")){
			node.doAction("createTreeItem", actionContext);
		}		
	}
	
	public static void createTreeItem(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		if(self.getStringBlankAsNull("refNode") != null){
			Thing refNode = World.getInstance().getThing(self.getString("refNode"));
			if(refNode != null){
				refNode.doAction("createTreeItem", actionContext);
				return;
			}
		}
		
		Object object = self.doAction("getObject", actionContext);
		if(object == null){
			return;
		}
		
		ActionContext context = (ActionContext) actionContext.get("context");
		//按照列表的方式创建
		Iterable<Object> iterable = UtilData.getIterable(object);
		for(Object obj : iterable){
			actionContext.peek().put("_object", obj);
			context.peek().put("_object", obj);
			String label = (String) self.doAction("getLabel", actionContext);
			String icon = (String) self.doAction("getIcon", actionContext);
			Object parentTreeItem = actionContext.get("treeItem");
			
			TreeItem item = null;
			if(parentTreeItem instanceof Tree){
				item = new TreeItem((Tree) parentTreeItem, SWT.None);
			}else{
				item = new TreeItem((TreeItem) parentTreeItem, SWT.NONE);
			}
			
			item.setData(obj);
			if(label == null){
				label = object.toString();
			}
			item.setText(label);
			
			//创建子节点
			actionContext.peek().put("treeItem", item);	
			context.peek().put("treeItem", item);	
			for(Thing child : self.getChilds("Node")){
				child.doAction("createTreeItem", actionContext);
			}
		
			if(icon == null || "".equals(icon)){
				if(item.getItemCount() == 0){
					item.setImage((Image) actionContext.get("fileImage"));
				}else{
					item.setImage((Image) actionContext.get("folderImage"));
				}
			}else{
				Image image = (Image) ResourceManager.createIamge(icon, actionContext);
				if(image != null){
					item.setImage(image);
				}
			}
		}
		
	}
	
	public static Object getNodeObject(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		Object obj = getValue(self.getString("objectPath"), actionContext);
		if(obj == null){
			return actionContext.get("_object");
		}else{
			return obj;
		}
	}
	
	public static Object getLabel(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		return getValue(self.getString("labelPath"), actionContext);
	}
	
	public static Object getIcon(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		return getValue(self.getString("iconPath"), actionContext);
	}
	
	public static Object getValue(String path, ActionContext actionContext) throws OgnlException{
		if(path == null || "".equals(path)){
			return null;
		}
		
		ActionContext context = (ActionContext) actionContext.get("context");
		
		return OgnlUtil.getValue(path, context);
	}
}
