package xworker.lang.actions;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class GetSelfActionValue {
	public static Object run(ActionContext actionContext) {
		Thing self = (Thing) actionContext.get("self");
		
		Thing parentSelf = null;
		List<Thing> things = actionContext.getThings();
        if(things.size() > 1){
        	parentSelf = things.get(things.size() - 2);
        }
		
        String actionName = self.doAction("getActionName", actionContext);
        if(parentSelf != null && actionName != null) {
        	return parentSelf.doAction(actionName, actionContext);
        }else {
        	return null;
        }
	}
}	
