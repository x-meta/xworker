package xworker.javafx.thing.editor.thingmenus;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.lang.executor.Executor;
import xworker.lang.executor.ExecutorService;
import xworker.lang.thingmenus.ExecuteAction;
import xworker.thingeditor.ThingMenu;

import java.util.List;
import java.util.Map;

public class ExecuteActionWithThingFormFX {
    public static void ok(ActionContext actionContext){
        javafx.stage.Stage stage = actionContext.getObject("stage");
        xworker.javafx.thing.form.ThingForm thingForm = actionContext.getObject("thingForm");

        ThingMenu thingMenu = actionContext.getObject("menu");
        Thing menuThing = thingMenu.getThing();
        String actionName = menuThing.doAction("getActionName", actionContext);
        Thing thing = actionContext.getObject("thing");

        Boolean background = menuThing.doAction("isBackground", actionContext);

        Map<String, Object> values = thingForm.getValues();
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

        stage.close();
    }

    public static void cancel(ActionContext actionContext){
        javafx.stage.Stage stage = actionContext.getObject("stage");
        stage.close();
    }
}


