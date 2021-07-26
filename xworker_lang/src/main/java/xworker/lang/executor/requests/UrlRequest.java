package xworker.lang.executor.requests;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ThingLoader;
import xworker.lang.executor.Request;

public class UrlRequest {
    public static Object create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        String platform = actionContext.getObject("platform");
        String url = self.doAction("getUrl", actionContext);

        if(Request.PLATFORM_SWT.equals(platform)){
            Thing prototype = World.getInstance().getThing("xworker.lang.executor.requests.prototypes.UrlRequest/@mainComposite");
            return prototype.doAction("create", actionContext, "url", url);
        }

        return null;
    }
}
