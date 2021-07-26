package xworker.net.jsch;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.jcraft.jsch.Session;

public class WithJschSessions {
	public static Object run(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Object result = null;
		for(Thing sessions : self.getChilds("Sessions")) {
			for(Thing sessionThing : sessions.getChilds()) {
				Session session = sessionThing.doAction("create", actionContext);
				try {
					for(Thing actions : self.getChilds("DoActions")) {
						for(Thing actionThing : actions.getChilds()) {
							result = actionThing.getAction().run(actionContext, "session", session, "sessionThing", sessionThing);
						}
					}
				}finally {
					session.disconnect();
				}
			}
		}
		
		return result;
	}
}
