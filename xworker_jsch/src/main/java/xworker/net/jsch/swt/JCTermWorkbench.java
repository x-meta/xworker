package xworker.net.jsch.swt;

import org.xmeta.ActionContext;

public class JCTermWorkbench {
    //xworker.net.jsch.swt.JCTermWorkbench/@Views/@logView/@Composite/@init
    public static void logViewInit(ActionContext actionContext){
        xworker.lang.executor.ExecutorService executorService = actionContext.getObject("executorService");
        xworker.swt.app.Workbench workbench = actionContext.getObject("workbench");

        if(executorService != null && workbench != null){
            workbench.setLogService(executorService);
        }
    }
}
