package xworker.swt.app.prototypes;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import xworker.swt.util.SwtUtils;

public class WorkbechShell {
    public static Thing getThingEditor(ActionContext actionContext){

        if(SwtUtils.isRWT() && !xworker.security.SecurityManager.doCheck("RAP",
                "XWorker_Thing_Manager", null, null, actionContext)) {
            return World.getInstance().getThing("xworker.swt.app.editors.ThingViewer");
        }else{
            return World.getInstance().getThing("xworker.swt.app.editors.ThingEditor");
        }
    }
}
