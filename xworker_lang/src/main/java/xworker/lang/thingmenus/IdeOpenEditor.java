package xworker.lang.thingmenus;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.util.XWorkerUtils;

import java.util.Map;

public class IdeOpenEditor {

    public static void ideOpenEditor(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        String id = self.doAction("getId", actionContext);
        Thing editor = self.doAction("getEditor", actionContext);
        Map<String, Object> params = self.doAction("getParams", actionContext);

        XWorkerUtils.openEditor(id, editor, params);
    }
}
