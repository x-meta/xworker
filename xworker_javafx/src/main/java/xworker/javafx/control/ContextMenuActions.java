package xworker.javafx.control;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ContextMenuActions {
    public static void init(ContextMenu box, Thing thing, ActionContext actionContext) {
        PopupControlActions.init(box, thing, actionContext);

    }

    public static ContextMenu create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        ContextMenu box = new ContextMenu();
        init(box, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), box);

        actionContext.peek().put("parent", box);
        for(Thing child : self.getChilds()) {
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof MenuItem){
                box.getItems().add((MenuItem) obj);
            }
        }

        return box;
    }
}
