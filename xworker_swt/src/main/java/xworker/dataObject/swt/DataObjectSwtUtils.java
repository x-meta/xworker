package xworker.dataObject.swt;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.swt.ActionContainer;
import xworker.swt.util.SwtDialog;
import xworker.swt.util.SwtUtils;

public class DataObjectSwtUtils {
    public static void menu_editData(ActionContext actionContext){
        World world = World.getInstance();
        Thing currentThing = (Thing) actionContext.get("currentThing");
        if(currentThing == null){
        	currentThing = actionContext.getObject("self");
        }
        
        ActionContext ac = new ActionContext();
        Control parent = (Control) actionContext.get("parent");
        if(parent != null){
        	ac.put("parent", parent.getShell());
        }
        
        Thing thing = world.getThing("xworker.dataObject.swt.DataObjectEditor/@shell");
        Shell newShell = (Shell) thing.doAction("create", ac);
        Thing queryConfig = world.getThing(currentThing.getString("defaultQueryConfig"));
        if(queryConfig == null){
        	queryConfig  = currentThing.getThing("Condition@0");
        }
        ((ActionContainer) ac.get("actions")).doAction("setDataObject", ac, UtilMap.toMap(new Object[]{"dataObject", currentThing, "queryConfig", queryConfig}));
        newShell.open();        
    }
    
    public static Object openSwtEditor(ActionContext actionContext){        
    	World world = World.getInstance();
        //Thing currentThing = (Thing) actionContext.get("currentThing");
        Control parent = (Control) actionContext.get("parent");
        
        //创建编辑窗体
        ActionContext ac = new ActionContext();
        ac.put("parent", parent);
        Thing editorThing = world.getThing("xworker.app.view.swt.widgets.form.DataObjectEditDialog/@shell");
        editorThing.doAction("create", ac);
        ((Thing) ac.get("form")).doAction("setDataObject", ac, UtilMap.toMap(new Object[]{"dataObject", actionContext.get("dataObject"), "formType", actionContext.get("type")}));
        Shell shell = (Shell) ac.get("shell");
        ((Shell) ac.get("shell")).pack();
        SwtUtils.centerShell((Shell) ac.get("shell"));
        
        SwtDialog dialog = new SwtDialog(shell.getShell(),shell, ac);
        return dialog.open();
    }
}
