package xworker.lang.thingmenus;

import freemarker.template.TemplateException;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import xworker.lang.executor.Executor;
import xworker.thingeditor.ThingMenu;
import xworker.util.UtilTemplate;

import java.io.IOException;

public class CodeWindow {
    private static final String TAG = CodeWindow.class.getName();

    public static void doAction(ActionContext actionContext) throws TemplateException, IOException {
        Thing self = actionContext.getObject("self");

        Thing thing = actionContext.getObject("thing");
        ThingMenu thingMenu = actionContext.getObject("menu");

        String template = self.doAction("getTemplate", actionContext);
        String codeType = self.doAction("getCodeType", actionContext);
        String actionName = self.doAction("getActionName", actionContext);

        String code = null;
        if(actionName != null){
            code = thing.doAction(actionName, actionContext);
        }
        if(code == null && template != null){
            //code = self.doAction("template", actionContext);
            code = UtilTemplate.processString(actionContext, template);
        }

        actionContext.g().put("codeType", codeType);
        actionContext.g().put("code", code);

        Thing window = null;
        if("javafx".equals(thingMenu.getEditorPlatform())){
            window = World.getInstance().getThing("xworker.javafx.thing.editor.thingmenus.CodeWindow");
        }else{
            window = World.getInstance().getThing("xworker.swt.xworker.editor.thingmenus.CodeWindow");
        }

        if(window == null){
            Executor.warn(TAG, "Can not open code window, cCode window thing is not found, platform=" + thingMenu.getEditorPlatform());
            return;
        }

        window.doAction("create", actionContext);
    }
}
