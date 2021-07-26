package xworker.swt.editor.thingmenus;

import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.lang.executor.Executor;
import xworker.lang.executor.ExecutorService;
import xworker.lang.thingmenus.ExecuteAction;
import xworker.swt.xworker.ThingForm;
import xworker.thingeditor.ThingMenu;

import java.util.List;
import java.util.Map;

public class ExecuteActionWithThingFormSwt {
    public static void init(ActionContext actionContext){
        org.xmeta.Thing thingForm = actionContext.getObject("thingForm");
        thingForm.doAction("setDescriptor", actionContext, "descriptor", actionContext.get("formThing"));

        Shell shell = actionContext.getObject("shell");
        ThingMenu thingMenu = actionContext.getObject("menu");
        Thing menuThing = thingMenu.getThing();
        //String actionName = menuThing.doAction("getActionName", actionContext);
        int width = menuThing.getInt("width", -1);
        int height = menuThing.getInt("height", -1);

        if(width > 0 || height > 0){
            shell.setSize(width, height);
        }
        shell.setVisible(true);
    }

    public static void execute(ActionContext actionContext){
        org.xmeta.Thing thingForm = actionContext.getObject("thingForm");
        ThingForm tf = ThingForm.getThingForm(thingForm);

        Shell shell = actionContext.getObject("shell");
        ThingMenu thingMenu = actionContext.getObject("menu");
        Thing menuThing = thingMenu.getThing();
        String actionName = menuThing.doAction("getActionName", actionContext);
        Thing thing = actionContext.getObject("thing");

        Boolean background = menuThing.doAction("isBackground", actionContext);
        Map<String, Object> values = tf.getValues();

        if(background != null && background){
            List<ExecutorService> executorServiceList = Executor.getExecutorServices();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Executor.push(executorServiceList);
                    actionContext.peek().putAll(values);
                    ExecuteAction.execute(thing, actionName, actionContext);
                }
            }).start();
        }else{
            actionContext.peek().putAll(values);
            ExecuteAction.execute(thing, actionName, actionContext);
        }

        shell.dispose();
    }
}
