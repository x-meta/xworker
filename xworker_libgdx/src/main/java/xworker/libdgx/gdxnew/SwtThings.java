package xworker.libdgx.gdxnew;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class SwtThings {
	public static void create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		for(Thing child : self.getChilds()){
			Object obj = child.doAction("create", actionContext);
			actionContext.getScope(0).put(child.getMetadata().getName(), obj);
		}
	}
}
