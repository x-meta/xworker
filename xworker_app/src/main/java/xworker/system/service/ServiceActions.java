package xworker.system.service;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ServiceActions {
	public static void doService(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		for(Thing group : self.getChilds()){
			for(Thing service : group.getChilds()){
				ServiceManager.request(service, actionContext);
			}
		}
	}
	
	public static void setServiceGroupData(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Object data = self.doAction("getData", actionContext);
		Thing serviceGroup = (Thing) self.doAction("getServiceGroup", actionContext);
		String key = (String) self.doAction("getKey", actionContext);
		
		ServiceGroup group = ServiceManager.getServiceGroupByGroup(serviceGroup);
		group.setData(key, data);
	}
}
