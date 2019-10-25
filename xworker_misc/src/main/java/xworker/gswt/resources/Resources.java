package xworker.gswt.resources;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class Resources {
	public static void create(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		for(Thing child : self.getChilds()){
			child.doAction("create", actionContext);
		}
	}
}	
