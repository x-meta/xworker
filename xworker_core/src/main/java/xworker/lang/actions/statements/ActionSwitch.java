package xworker.lang.actions.statements;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

public class ActionSwitch {
    public static Object run(ActionContext actionContext) {
    	Thing self = actionContext.getObject("self");
    	
    	
    	Object result = null;
    	String r = null;
    	try {
    		result = self.doAction("doAction", actionContext);
    		r = String.valueOf(result);
    	}catch(Throwable t) {
    		result = t;
    		r = "exception";
    	}    	
    	
    	for(Thing child : self.getChilds()) {
    		if(r.equals(child.getMetadata().getName())) {
    			return child.getAction().run(actionContext, "result", result);
    		}
    	}
    	
    	if(result instanceof Throwable) {
    		throw new ActionException((Throwable) result);
    	}
    	return null;
    }
}
