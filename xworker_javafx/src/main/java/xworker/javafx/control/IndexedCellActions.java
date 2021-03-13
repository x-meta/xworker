package xworker.javafx.control;

import javafx.scene.control.IndexedCell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class IndexedCellActions {
    public static void init(IndexedCell<Object> node, Thing thing, ActionContext actionContext){
        CellActions.init(node, thing, actionContext);
    }
}
