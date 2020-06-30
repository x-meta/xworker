package xworker.lang.actions;

import java.util.HashMap;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ActionContainerActions {
	//xworker.lang.actions.ActionContainerAction/@actions/@run
	public static Object run(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ActionContainer ac = self.doAction("getActionContainer", actionContext);
		String action = self.doAction("getAction", actionContext);
		
		Map<String, Object> params = new HashMap<String, Object>();
		for(Thing child : self.getChilds()) {
			params.put(child.getMetadata().getName(), child.getAction().run(actionContext));
		}
		
		Map<String, Object> variables = self.doAction("getVariables", actionContext);
		if(variables != null) {
			params.putAll(variables);
		}
		
		return ac.doAction(action, params);
	}
}
