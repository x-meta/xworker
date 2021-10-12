package xworker.swt.design;

import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.ActionContainer;

public class DesignerActions {

    //xworker.swt.design.DesignActions/@Init/@actions/@run
    public static Object init(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        //注册toolActions
        ActionContainer actions = self.doAction("getActionContainer", actionContext);

        ActionContext parentContext = actionContext.getObject("parentContext");
        parentContext.g().put("toolActions", actions);

        return actions.doAction("handleNewControl", actionContext, "newControl", parentContext.get("control"));
    }

    //xworker.swt.design.DesignActions/@ReturnHome/@actions/@run
    public static void returnHome(ActionContext actionContext){
        ActionContext parentContext = actionContext.getObject("parentContext");

        //重置参数
        parentContext.g().put("toolActions", null);
        parentContext.g().put("currentTool", null);

        ActionContainer actions = parentContext.getObject("actions");
        actions.doAction("reInit");

        Shell shell = parentContext.getObject("shell");
        if(!shell.isDisposed()){
            shell.forceActive();
        }
    }

    //xworker.swt.design.DesignActions/@Enable
    public static void enable(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Boolean enable = self.doAction("isEnable", actionContext);
        Designer.setEnabled(enable);
    }
}
