package xworker.swt.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import ognl.OgnlException;
import xworker.app.model.tree.TreeModel;
import xworker.app.model.tree.TreeModelItem;
import xworker.app.model.tree.TreeModelUtils;
import xworker.app.model.tree.impl.ThingTreeModelActions;

public class AppTreeModel {
	/**
	 * 树模型的返回子节点列表的实现。
	 */
	public static Object getChilds(ActionContext actionContext) throws IOException, OgnlException {
		Thing self = (Thing) actionContext.get("self");
		TreeModel treeModel = actionContext.getObject("treeModel");
		TreeModelItem parentItem = actionContext.getObject("parentItem");

		if(parentItem.getSource() == self) {
			List<TreeModelItem> items = new ArrayList<>();
			for(Thing child : self.getChilds()){
				if(child.isThing("xworker.swt.app.AppTreeMenuNode")){
					items.add(TreeModelUtils.toItem(treeModel, parentItem, child));
				}
			}

			return items;

		}else{
			Thing node = parentItem.getSource();
			return node.doAction("getChilds", actionContext);
		}
	}
	
	public static List<TreeModelItem> getMenuChilds(ActionContext actionContext){
		Thing self  = actionContext.getObject("self");
		TreeModel treeModel = actionContext.getObject("treeModel");
		TreeModelItem parentItem = actionContext.getObject("parentItem");

		List<TreeModelItem> items = new ArrayList<>();
		for(Thing child : self.getChilds()){
			if(child.isThing("xworker.swt.app.AppTreeMenuNode")){
				items.add(TreeModelUtils.toItem(treeModel, parentItem, child));
			}
		}

		return items;
	}

}
