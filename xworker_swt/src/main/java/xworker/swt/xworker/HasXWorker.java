package xworker.swt.xworker;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.util.XWorkerUtils;

public class HasXWorker {
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Thing parentThing = null;
		if(XWorkerUtils.hasXWorker()) {
			parentThing = self.getThing("Then@0");			
		}else {
			parentThing = self.getThing("Else@0");			
		}
		
		if(parentThing != null) {
			Object result = null;
			for(Thing child : parentThing.getChilds()) {
				result = child.doAction("create", actionContext);
			}
			return result;
		}else {
			return null;
		}
	}
}
