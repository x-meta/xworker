package xworker.swt.app.treeMenuNodes;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;
import xworker.lang.executor.Executor;

public class OpenQucikContentEditor {
    private static final String TAG = OpenQucikContentEditor.class.getName();

    public static void run(ActionContext actionContext) {
        World world = World.getInstance();
        Thing self = actionContext.getObject("self");
        Thing thing = self.doAction("getQuickContent", actionContext);
        Action openEditor = world.getAction("xworker.swt.app.treeMenuNodes.OpenEditor/@actions1/@run/@ActionDefined/@openEditor");
        Thing editor = world.getThing("xworker.swt.app.editors.QuickContentEditor");
        if(thing != null){
            openEditor.run(actionContext, "id", thing.getMetadata().getPath(),
                    "editor", editor,
                    "params", UtilMap.toMap("thing", thing));
        }else{
            Executor.info(TAG, "Thing not exists, path=" + self.getMetadata().getPath());
        }
    }
}
