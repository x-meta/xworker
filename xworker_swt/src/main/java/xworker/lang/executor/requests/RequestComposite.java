package xworker.lang.executor.requests;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.executor.Executor;

public class RequestComposite {
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Thing composite = self.doAction("getComposite", actionContext);
		if(composite == null) {
			Executor.warn("RequestComposite", "Composite is null, path=" + self.getMetadata().getPath());
			return null;
		}else {
			return composite.doAction("create", actionContext);
		}
	}
}
