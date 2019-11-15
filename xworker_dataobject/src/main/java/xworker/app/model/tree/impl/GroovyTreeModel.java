package xworker.app.model.tree.impl;

import java.util.HashMap;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class GroovyTreeModel {
	public static Object getRoot(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Object childs =  self.getAction().run(actionContext);
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("childs", childs);
		return root;
	}
}
