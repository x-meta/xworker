package xworker.javafx.control;

import javafx.scene.control.TableCell;
import javafx.scene.control.TreeTableCell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class TreeTableCellActions {
    public static void init(TreeTableCell node, Thing thing, ActionContext actionContext){
        IndexedCellActions.init(node, thing, actionContext);
    }

    public static TreeTableCell create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        TreeTableCell node  = new ThingTreeTableCell(self, actionContext);
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
        return node;
    }

    public static class ThingTreeTableCell<S, T> extends TreeTableCell<S, T>{
        Thing thing;
        ActionContext actionContext;

        public ThingTreeTableCell(Thing thing, ActionContext actionContext){
            this.thing = thing;
            this.actionContext = actionContext;
        }

        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);

            thing.doAction("updateItem", actionContext, "cell", this, "item", item, "empty", empty);
        }
    }
}
