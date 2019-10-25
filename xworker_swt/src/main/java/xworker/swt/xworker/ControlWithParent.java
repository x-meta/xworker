package xworker.swt.xworker;

import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

public class ControlWithParent {
	
	public static void create(ActionContext actionContext) {
		 Thing self = actionContext.getObject("self");
		 
		 Object parent = self.doAction("getParent", actionContext);
		 
		 if(parent == null || !(parent instanceof Widget)) {
			 throw new ActionException("Parent is null or parent is not a WIdget, thing=" + self.getMetadata().getPath());
		 }
		 
		 ActionContext ac = self.doAction("getActionContext", actionContext);
		 ac.peek().put("parent", parent);
		 for(Thing child : self.getChilds()) {
			 child.doAction("create", ac);
		 }
	}
}
