package xworker.lang.executor.requests;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import xworker.lang.executor.Request;

public class HtmlRequest {
    public static Object create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        String platform = actionContext.getObject("platform");
        String html = self.doAction("getHtml", actionContext);
        if(Request.PLATFORM_SWT.equals(platform)){
            Thing thing = World.getInstance().getThing("xworker.lang.executor.requests.prototypes.Html/@mainComposite");
            return thing.doAction("create", actionContext, "html", html);
        }

        return null;
    }
}
