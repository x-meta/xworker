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
import xworker.app.model.tree.impl.ThingTreeModelActions;

public class AppTreeModel {
	/**
	 * 树模型的返回子节点列表的实现。
	 * 
	 * @param actionContext
	 * @return
	 * @throws IOException
	 * @throws OgnlException
	 */
	public static Object getChilds(ActionContext actionContext) throws IOException, OgnlException {
		Thing self = (Thing) actionContext.get("self");

		//返回当前节点
		String id = (String) actionContext.get("id");
		if (id == null || "".equals(id) || TreeModel.ROOT_ID.equals(id)) {
			List<Object> childs = new ArrayList<Object>();
			childs.add(ThingTreeModelActions.getThingRoot(actionContext));
			return childs;
		}

		//获取当前节点下的子节点
		Thing thing = World.getInstance().getThing(id);
		if (thing != null) {
			List<Thing> childs = thing.doAction("getMenuChilds", actionContext);
			List<Map<String, Object>> childNodes = new ArrayList<Map<String, Object>>();
			if (childs != null) {
				for (Thing child : childs) {
					childNodes.add(ThingTreeModelActions.toTreeNode(child, self, null, null, actionContext));
				}
			}
			
			return childNodes;
		} else {
			return null;
		}
	}
	
	public static List<Thing> getMenuChilds(ActionContext actionContext){
		Thing self  = actionContext.getObject("self");
		return self.getChilds("AppTreeMenuNode");
	}
	
	public static Object getRoot(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		return ThingTreeModelActions.toTreeNode(self, self, null, null, actionContext);
	}
}
