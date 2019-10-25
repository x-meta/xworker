package xworker.util;

import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingRegistActions {
	public static List<Thing> searchRegistedThings(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
	
		Thing registThing = self.doAction("getThing", actionContext);
		String type = self.doAction("getType", actionContext);
		String keys = self.doAction("getKeys", actionContext);
		
		List<String> ks = new ArrayList<String>();
		if(keys != null && !"".equals(keys)){
			for(String key : keys.split("[,]")){
				ks.add(key);
			}
		}
		
		//查找注册的事物列表
		return ThingUtils.searchRegistThings(registThing, type, ks, actionContext);
	}
	
	public static void executeRegistedThings(ActionContext actionContext){
		List<Thing> things = searchRegistedThings(actionContext);
		
		Thing self = actionContext.getObject("self");
		String actions = self.doAction("getActionName", actionContext);
		
		if(actions != null && !"".equals(actions)){
			String[] acs = actions.split("[,]");
			for(int i=0; i<acs.length; i++){
				acs[i] = acs[i].trim();
			}
			for(Thing thing : things){
				for(String action : acs){
					thing.doAction(action, actionContext);
				}
			}
		}
	}
}
