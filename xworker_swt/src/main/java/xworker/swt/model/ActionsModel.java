package xworker.swt.model;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ActionsModel {
	public static void create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		for(Thing child : self.getChilds()) {
			Object obj = child.getAction().run(actionContext);
			actionContext.g().put(child.getMetadata().getName(), obj);
		}
	}
}
