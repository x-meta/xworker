package xworker.swt.xwidgets;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class Node {
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Object obj = null;
		for(Thing child : self.getChilds()) {
			obj = child.doAction("create", actionContext);
		}
		
		return obj;
	}
}
