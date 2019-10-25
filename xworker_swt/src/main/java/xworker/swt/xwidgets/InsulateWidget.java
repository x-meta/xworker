package xworker.swt.xwidgets;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class InsulateWidget {
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ActionContext ac = new ActionContext();
		ac.put("parent", actionContext.get("parent"));
		ac.put("parentContext", actionContext);
		
		Object result = null;
		for(Thing child : self.getChilds()) {
			result = child.doAction("create", ac);
		}
		
		actionContext.g().put(self.getMetadata().getName(), ac);
		return result;
	}
}
