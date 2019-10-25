package xworker.lang.actions.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;

public class Sl4jLogger {
	public static Logger run(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String tagName = self.doAction("getTagName", actionContext);
		if(tagName == null || "".equals(tagName)) {
			tagName = self.getMetadata().getPath();
		}
		
		if(UtilData.isTrue(self.doAction("isAction", actionContext))) {
			Action action = World.getInstance().getAction(tagName);
			if(action != null) {
				tagName = action.className;
			}
		}
		
		return LoggerFactory.getLogger(tagName);
	}
	
	public static String getActionClassName(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Thing action = self.doAction("getAction", actionContext);
		if(action != null) {
			return action.getAction().className;
		}else {
			return null;
		}
	}
}
