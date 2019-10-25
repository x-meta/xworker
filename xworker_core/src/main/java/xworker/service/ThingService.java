package xworker.service;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.actions.ActionContainer;

public class ThingService {
	public static void regist(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ActionContainer ac = new ActionContainer(self, actionContext);
		Service service = new Service(self, ac);
		
		String serviceName = self.doAction("getServiceName", actionContext);
		ServiceManager.regist(serviceName, service);
	}
}
