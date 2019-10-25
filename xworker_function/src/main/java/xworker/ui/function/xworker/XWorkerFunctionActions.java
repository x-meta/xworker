package xworker.ui.function.xworker;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;

public class XWorkerFunctionActions {
	public static Thing getThing(ActionContext actionContext){
		String thingPath = (String) actionContext.get("thingPath");
		if(thingPath == null){
			throw new ActionException("thing path is null");
		}
		
		return World.getInstance().getThing(thingPath);
	}
	
	public static Thing thingAddChild(ActionContext actionContext){
		Thing thing = (Thing) actionContext.get("thing");
		Thing child = (Thing) actionContext.get("child");		
		thing.addChild(child);
		
		return thing;
	}
}
