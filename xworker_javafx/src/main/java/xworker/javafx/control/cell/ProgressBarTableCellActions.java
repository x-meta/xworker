package xworker.javafx.control.cell;

import javafx.scene.control.cell.ProgressBarTableCell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.control.TableCellActions;

public class ProgressBarTableCellActions {
    public static void init(ProgressBarTableCell<Object> node, Thing thing, ActionContext actionContext){
        TableCellActions.init(node, thing, actionContext);
    }

    public static ProgressBarTableCell<Object> create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        ProgressBarTableCell<Object> node = new ProgressBarTableCell<>();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }
}
