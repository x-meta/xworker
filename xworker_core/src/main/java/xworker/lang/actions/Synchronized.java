package xworker.lang.actions;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

public class Synchronized {
	@SuppressWarnings("unchecked")
	public static Object run(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		Object obj = self.doAction("getObject", actionContext);
		if(obj == null){
			throw new ActionException("Object for synchronized is null, action=" + self.getMetadata().getPath());
		}
		Object result = null;
        actionContext.peek().setVarScopeFlag(); //设置局部变量范围的标识
        for(Thing actions :(List<Thing>) self.get("ChildAction@")){
            //log.info("Action: " + actions);
            for(Thing action : actions.getChilds()){      
                result = action.getAction().run(actionContext, null, true);
        
                if(ActionContext.RETURN == actionContext.getStatus() || 
                    ActionContext.CANCEL == actionContext.getStatus() || 
                    ActionContext.BREAK == actionContext.getStatus() || 
                    ActionContext.EXCEPTION == actionContext.getStatus() ||
                    ActionContext.CONTINUE == actionContext.getStatus()){
                    break;
                }
            } 
            if(ActionContext.RETURN == actionContext.getStatus() || 
                ActionContext.CANCEL == actionContext.getStatus() || 
                ActionContext.BREAK == actionContext.getStatus() || 
                ActionContext.EXCEPTION == actionContext.getStatus() ||
                ActionContext.CONTINUE == actionContext.getStatus()){
                break;
            }
        }
        
        return result;
	}
}
