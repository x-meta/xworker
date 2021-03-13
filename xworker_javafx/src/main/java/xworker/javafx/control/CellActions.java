package xworker.javafx.control;

import javafx.scene.control.Cell;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;

public class CellActions {
    public static void init(Cell<Object> node, Thing thing, ActionContext actionContext){
        LabeledActions.init(node, thing, actionContext);

        if(thing.valueExists("editable")){
            node.setEditable(thing.getBoolean("editable"));
        }
        if(thing.valueExists("item")){
            Object item = JavaFXUtils.getObject(thing, "item", actionContext);
            node.setItem(item);
        }
    }

    public static Cell<Object> create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Cell<Object> node = new Cell<Object>();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()) {
            child.doAction("create", actionContext);
        }

        return node;
    }
}
