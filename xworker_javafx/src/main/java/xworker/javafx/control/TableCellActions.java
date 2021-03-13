package xworker.javafx.control;

import javafx.scene.control.TableCell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class TableCellActions {
    public static void init(TableCell node, Thing thing, ActionContext actionContext){
        IndexedCellActions.init(node, thing, actionContext);
    }

    public static TableCell create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        TableCell node  = new ThingTableCell(self, actionContext);
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
        return node;
    }

    public static class ThingTableCell<S, T> extends TableCell<S, T>{
        Thing thing;
        ActionContext actionContext;

        public ThingTableCell(Thing thing, ActionContext actionContext){
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
