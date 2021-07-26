package xworker.lang.executor.requests;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import xworker.lang.executor.Request;

public class ThingDescRequest {
    public static Object create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        String platform = actionContext.getObject("platform");
        Thing thing = self.doAction("getThing", actionContext);
        if(thing == null){
            thing = self;
        }

        if(Request.PLATFORM_SWT.equals(platform)){
            Thing prototype = World.getInstance().getThing("xworker.lang.executor.requests.prototypes.ThingDesc/@mainComposite");
            return prototype.doAction("create", actionContext, "thing", thing);
        }

        return null;
    }
}
