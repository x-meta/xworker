package xworker.javafx.control;

import javafx.scene.control.ListCell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ListCellActions {
    public static void init(ListCell<Object> node, Thing thing, ActionContext actionContext){
        IndexedCellActions.init(node, thing, actionContext);
    }

    public static ListCell<Object> create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        ListCell<Object> node = new ThingListCell<Object>(self, actionContext);
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }

    public static class ThingListCell<T> extends ListCell<T>{
        Thing thing;
        ActionContext actionContext;

        public ThingListCell(Thing thing, ActionContext actionContext){
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
