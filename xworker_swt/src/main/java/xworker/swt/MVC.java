package xworker.swt;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class MVC {
	public static void create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		actionContext.peek().put("parent", null);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext);
		}
	}
}
