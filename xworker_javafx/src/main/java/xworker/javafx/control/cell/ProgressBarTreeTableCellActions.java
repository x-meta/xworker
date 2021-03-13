package xworker.javafx.control.cell;

import javafx.scene.control.cell.ProgressBarTreeTableCell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.control.TreeTableCellActions;

public class ProgressBarTreeTableCellActions {
    public static void init(ProgressBarTreeTableCell<Object> node, Thing thing, ActionContext actionContext){
        TreeTableCellActions.init(node, thing, actionContext);
    }

    public static ProgressBarTreeTableCell<Object> create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        ProgressBarTreeTableCell<Object> node = new ProgressBarTreeTableCell<>();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }
}
