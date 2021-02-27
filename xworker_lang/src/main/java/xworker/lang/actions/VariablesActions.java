package xworker.lang.actions;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;

public class VariablesActions {
	public static void setLocalVariables(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Bindings bindings = actionContext.l(self.getInt("index"));
		if(bindings == null){
			throw new ActionException("Local bindings not exists, index=" + self.getInt("index"));
		}
		
		for(Thing child : self.getChilds()){
			bindings.put(child.getMetadata().getName(), child.getAction().run(actionContext, null, true));
		}
	}
	
	public static void setGloablVariables(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Bindings bindings = actionContext.g();
		
		for(Thing child : self.getChilds()){
			bindings.put(child.getMetadata().getName(), child.getAction().run(actionContext, null, true));
		}
	}
	
	public static Object setObjectAttributes(ActionContext actionContext) throws OgnlException{
		Thing self = actionContext.getObject("self");
		
		Object object = self.doAction("getObject", actionContext);
		if(object == null){
			throw new ActionException("Object is null, action=" + self.getMetadata().getPath());
		}
		
		boolean firstActions = true;
		for(Thing child : self.getChilds()){
			if(firstActions){
				String thingName = child.getThingName();
				if("actions".equals(thingName)){
					firstActions = false;
					continue;
				}
			}
			
			Object value = child.getAction().run(actionContext, null, true);
			OgnlUtil.setValue(child.getMetadata().getName(), object, value);
		}
		
		return object;
	}
}
