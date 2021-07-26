package xworker.lang.thingmenus;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.lang.executor.Executor;
import xworker.lang.executor.ExecutorService;
import xworker.task.UserTask;
import xworker.task.UserTaskListener;
import xworker.task.UserTaskManager;
import xworker.util.Callback;
import xworker.util.IIde;
import xworker.util.XWorkerUtils;

import java.util.List;

public class ExecuteAction {
    public static void doAction(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Thing thing = actionContext.getObject("thing");

        String actionName = self.doAction("getActionName", actionContext);
        Boolean background = self.doAction("isBackground", actionContext);
        Boolean confirm = self.doAction("isConfirm", actionContext);
        String confirmMessage = self.doAction("getConfirmMessage", actionContext);
        if(confirm != null && confirm){
            XWorkerUtils.ideShowMessageBox(self.getMetadata().getLabel(), confirmMessage, IIde.ICON_QUESTION | IIde.YES | IIde.NO,
                    new Callback<Integer, Void>() {
                @Override
                public Void call(Integer integer) {
                    if(integer == IIde.YES){
                        execute(thing, actionName, background, actionContext);
                    }
                    return null;
                }
            });
        }else{
            execute(thing, actionName, background, actionContext);
        }


    }

    public static void execute(Thing  thing, String actionName, Boolean background, ActionContext actionContext){
        if(background != null && background){
            List<ExecutorService> executorServiceList = Executor.getExecutorServices();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserTask userTask = UserTaskManager.createTask(thing, false);
                    try {
                        userTask.start();
                        userTask.set("Executting action " + actionName, thing.getMetadata().getDescription());
                        Executor.push(executorServiceList);

                        execute(thing, actionName, actionContext);
                    }finally {
                        userTask.finished();
                    }
                }
            }).start();
        }else{
            execute(thing, actionName, actionContext);
        }
    }

    public static void execute(Thing  thing, String actionName, ActionContext actionContext){
        String TAG = "ExecuteAction";

        try{
            Executor.info(TAG, "Executing action, thing=" + thing.getMetadata().getPath() + ", action=" + actionName);
            Object result = thing.doAction(actionName,  actionContext);
            Executor.info(TAG, "Execution finished, result=" + result);
        }catch(Exception e){
            Executor.info(TAG, "Execution failed", e);
        }
    }
}
