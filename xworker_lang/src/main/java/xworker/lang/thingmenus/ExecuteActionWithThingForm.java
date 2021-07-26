package xworker.lang.thingmenus;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import xworker.lang.executor.Executor;
import xworker.thingeditor.ThingMenu;

public class ExecuteActionWithThingForm {
    private static final String TAG = ExecuteActionWithThingForm.class.getName();

    public static void doAction(ActionContext actionContext) {
        Thing self = actionContext.getObject("self");

        Thing thing = actionContext.getObject("thing");
        ThingMenu thingMenu = actionContext.getObject("menu");

        Thing formThing = self.doAction("getThing", actionContext);
        actionContext.g().put("formThing", formThing);

        Thing window = null;
        if ("javafx".equals(thingMenu.getEditorPlatform())) {
            window = World.getInstance().getThing("xworker.javafx.thing.editor.thingmenus.ExecuteActionWithThingForm");
        } else {
            window = World.getInstance().getThing("xworker.swt.xworker.editor.thingmenus.ExecuteActionWithThingForm");
        }

        if (window == null) {
            Executor.warn(TAG, "Can not execute action with form, execute window thing is not found, platform=" + thingMenu.getEditorPlatform());
            return;
        }

        window.doAction("create", actionContext);
    }
}
