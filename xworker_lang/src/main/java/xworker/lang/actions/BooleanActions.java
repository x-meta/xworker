package xworker.lang.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

public class BooleanActions {
	public static boolean isNull(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Object value = getValue(self, actionContext);
		return value == null;
	}
	
	public static boolean isNotNull(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Object value = getValue(self, actionContext);
		return value != null;
	}
	
	public static Object getValue(Thing self, ActionContext actionContext) {
		ActionsFilter actions = new ActionsFilter(self.getChilds(), "value");
		if(actions.exists("value")) {
			return actions.run("value", actionContext, false);
		}else {
			return self.doAction("getValue", actionContext);
		}
	}
	
	public static boolean isTrue(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Object value = getValue(self, actionContext);
		return UtilData.isTrue(value);
	}
	
	public static boolean isNotTrue(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Object value = getValue(self, actionContext);
		return !UtilData.isTrue(value);
	}
	
	public static boolean and(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		for(Thing ac : self.getChilds()){
			if(UtilData.isTrue(ac.getAction().run(actionContext)) == false){
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean or(ActionContext actionContext){
		Thing self = actionContext.getObject("self");

		for(Thing ac : self.getChilds()){
			if(UtilData.isTrue(ac.getAction().run(actionContext))){
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean not(ActionContext actionContext){
		Thing self = actionContext.getObject("self");

		for(Thing ac : self.getChilds()){
			return !UtilData.isTrue(ac.getAction().run(actionContext));
		}
		
		return false;
	}
}
