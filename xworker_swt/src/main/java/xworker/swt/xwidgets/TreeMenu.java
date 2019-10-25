package xworker.swt.xwidgets;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.design.Designer;
import xworker.swt.util.XWorkerTreeUtil;

public class TreeMenu {
	public static Object create(ActionContext actionContext) {
		Thing self = (Thing) actionContext.get("self");
		
		//创建演示的TabFolder
		Thing thing = World.getInstance().getThing("xworker.swt.xwidgets.prototypes.TreeMenuShell/@tree");
		
		ActionContext ac = new ActionContext();
		ac.put("parent", actionContext.get("parent"));
		ac.put("parentContext", actionContext);
		Tree tree = null;
		Designer.pushCreator(self);
		try{
			tree = thing.doAction("create", ac);
		}finally{
			Designer.popCreator();
		}
		tree.setData(self);
		//创建子节点
		actionContext.peek().put("parent", tree);
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
				
		//创建树节点
		for(Thing menuItem : self.getChilds("MenuItem")) {
			TreeItem treeItem = new TreeItem(tree, SWT.NONE);			
			initTreeItem(treeItem, menuItem, ac);
		}
				
		Designer.attachCreator((Control) tree, self.getMetadata().getPath(), actionContext);
		actionContext.getScope(0).put(self.getMetadata().getName(), tree);
		return tree;
	}
	
	public static void initTreeItem(TreeItem treeItem, Thing itemThing, ActionContext actionContext) {
		treeItem.setData(itemThing);
		XWorkerTreeUtil.initItem(treeItem, itemThing, actionContext);
		
		//创建子节点
		List<Thing> childs = itemThing.doAction("getChilds", actionContext);
		if(childs != null) {
			for(Thing child : childs) {
				TreeItem item = new TreeItem(treeItem, SWT.NONE);
				initTreeItem(item, child, actionContext);
			}
		}
	}
	
	
	public static List<Thing> getChilds(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		return self.getChilds("MenuItem");
	}
}
