package xworker.javafx.control;

import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;

public class TreeCellActions {
    public static void init(TreeCell<Object> node, Thing thing, ActionContext actionContext){
        IndexedCellActions.init(node, thing, actionContext);

        if(thing.valueExists("disclosureNode")){
            Node disclosureNode = JavaFXUtils.getObject(thing, "disclosureNode", actionContext);
            if(disclosureNode != null){
                node.setDisclosureNode(disclosureNode);
            }
        }
    }

    public static void createDisclosureNode(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        TreeCell<?> parent = actionContext.getObject("parent");

        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof  Node){
                parent.setDisclosureNode((Node) obj);
            }
        }
    }

    public static TreeCell<Object> create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        TreeCell node  = new ThingTreeCell(self, actionContext);
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
        return node;
    }

    public static class ThingTreeCell<T> extends TreeCell<T> {
        Thing thing;
        ActionContext actionContext;

        public ThingTreeCell(Thing thing, ActionContext actionContext){
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
