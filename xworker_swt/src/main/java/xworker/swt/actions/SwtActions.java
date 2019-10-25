package xworker.swt.actions;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.util.SwtUtils;
import xworker.util.UtilAction;

public class SwtActions {
	public static boolean isRWT(ActionContext actionContext) {
		return SwtUtils.isRWT();
	}
	
	public static Object onRWT(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
        //self变量改名
        if(self.getBoolean("changeSelf")){  
            List<Thing> things = actionContext.getThings();
            Thing s = null;
            if(things.size() > 1){
                s = things.get(things.size() - 2);
            }
            actionContext.peek().put(self.getString("selfVarName"), s);
        }
        
		if(SwtUtils.isRWT()) {
			Thing then = self.getThing("Then@0");
			if(then != null) {
				return UtilAction.runChildActions(then.getChilds(), actionContext, true);
			}			
		}else {
			Thing elset = self.getThing("Else@0");
			if(elset != null) {
				return UtilAction.runChildActions(elset.getChilds(), actionContext, true);
			}
		}
		
		return null;
	}
	
	public static Object isRWTCreate(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		if(SwtUtils.isRWT()) {
			Thing then = self.getThing("Then@0");
			if(then != null) {
				Object r = null;
				for(Thing child : then.getChilds()) {
					r = child.doAction("create", actionContext);					
				}
				return r;
			}			
		}else {
			Thing elset = self.getThing("Else@0");
			if(elset != null) {
				Object r = null;
				for(Thing child : elset.getChilds()) {
					r = child.doAction("create", actionContext);					
				}
				return r;
			}
		}
		
		return null;
	}
}
