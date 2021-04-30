package xworker.swt.xworker;

import org.eclipse.swt.browser.Browser;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.swt.util.SwtUtils;

public class RapThingViewer {
    public static void selection(ActionContext actionContext){
        Thing thing = actionContext.getObject("thing");
        Browser browser = actionContext.getObject("outlineBrowser");
        if(thing != null && browser != null){
            SwtUtils.setThingDesc(thing, browser);
        }
    }
}
