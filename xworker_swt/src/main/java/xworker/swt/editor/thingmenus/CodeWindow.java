package xworker.swt.editor.thingmenus;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;

public class CodeWindow {
    public static void create(ActionContext actionContext){
        ActionContext parentContext = actionContext.getObject("parentContext");

        ToolBar toolBar = parentContext.getObject("toolBar");

        Thing thing = World.getInstance().getThing("xworker.swt.xworker.dialogs.CodeViewer");
        Shell shell = thing.doAction("create", actionContext, "parent", toolBar.getShell());

        ActionContainer actionContainer = actionContext.getObject("actions");
        actionContainer.doAction("setCode", actionContext);

        shell.setVisible(true);
    }
}
