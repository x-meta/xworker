package xworker.lang.thingmenus;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.util.XWorkerUtils;

import java.util.Map;

public class IdeActions {

    public static void ideOpenView(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        String id = self.doAction("getId", actionContext);
        Thing view = self.doAction("getView", actionContext);
        String type = self.doAction("getType", actionContext);
        Map<String, Object> params = self.doAction("getParams", actionContext);
        Boolean closeable = self.doAction("isCloseable", actionContext);

        XWorkerUtils.openView(id, type, closeable, view, params);
    }
}
