package xworker.lang.actions.java;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import ognl.Ognl;
import ognl.OgnlException;

public class SetObjectValues {
	public static Object run(ActionContext actionContext) throws OgnlException {
		Thing self = actionContext.getObject("self");
		
		Object obj = self.doAction("getObject", actionContext);
		for(Thing child : self.getChilds()) {
			String name = child.getMetadata().getName();
			
			Object value = child.getAction().run(actionContext);
			
			Ognl.setValue(name, obj, value);
		}
		
		return obj;
	}
}
