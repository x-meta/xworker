package xworker.lang.actions;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

public class Condition {
	public static boolean run(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		boolean not = self.doAction("isNot", actionContext);
		String join = self.doAction("getJoin", actionContext);
		if(join == null) {
			join = "or";
		}
		boolean or = "or".equals(join);
		
		boolean result = true;
		for(Thing child : self.getChilds()) {
			result = UtilData.isTrue(child.getAction().run(actionContext));
			
			if(or) {
				if(result) {
					break;
				}
			}else if(!result) {
				break;
			}
		}
		
		if(not) {
			return !result;
		}else {
			return result;
		}
		
	}
}
