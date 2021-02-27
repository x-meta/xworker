package xworker.lang.actions;

import java.util.HashMap;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

public class GetParentContextValue {
	public static Object run(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		String varName = (String) self.doAction("getVarName", actionContext);
		if(varName == null || "".equals(varName)){
			throw new ActionException("varName is null, action=" + self.getMetadata().getPath());
		}
		
		Object value = null;
		ActionContext ac = actionContext;
		Map<ActionContext, ActionContext> context  = new HashMap<ActionContext, ActionContext>();
		while(ac != null){
			//避免递归，加入context判断
			if(context.get(ac) != null) {
				break;
			}else {
				context.put(ac, ac);
			}
			
			value = ac.get(varName);
			if(value != null){			   
			    return value;
			}else if(ac.containsKey(varName)){
				return null;
			}
								
			Object acParent = ac.getObject("parentActionContext");
			if(acParent instanceof ActionContext){
				ac = (ActionContext) acParent;
				continue;
			}
			
			acParent = ac.getObject("parentContext");
			if(acParent instanceof ActionContext){
				ac = (ActionContext) acParent;
				continue;
			}
		}
		
		return null;
	}
}
