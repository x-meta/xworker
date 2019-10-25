package xworker.lang.actions.statements;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ClassSwitch {
	public static Object run(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Object object = self.doAction("getObject", actionContext);
		List<String> names = new ArrayList<String>();
		if(object != null) {
			for(Class<?> cls : object.getClass().getDeclaredClasses()) {
				names.add(cls.getSimpleName());
			}			
		}else {
			names.add("null");
		}
		
		for(Thing child : self.getChilds()) {
			String name = child.getMetadata().getName();
			for(String n : names) {
				if(name.equals(n)) {
					return child.getAction().run(actionContext, "object", object);
				}
			}
		}
		
		return null;
	}
}
