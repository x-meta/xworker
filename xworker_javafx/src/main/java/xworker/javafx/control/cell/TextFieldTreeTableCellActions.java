package xworker.javafx.control.cell;

import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.util.StringConverter;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.control.TreeTableCellActions;
import xworker.javafx.util.JavaFXUtils;

public class TextFieldTreeTableCellActions {
    public static void init(TextFieldTreeTableCell<Object, Object> node, Thing thing, ActionContext actionContext){
        TreeTableCellActions.init(node, thing, actionContext);

        if(thing.valueExists("converter")){
            StringConverter<Object> converter = JavaFXUtils.getObject(thing, "converter", actionContext);
            if(converter != null) {
                node.setConverter(converter);
            }
        }
    }

    public static TextFieldTreeTableCell<Object, Object> create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        TextFieldTreeTableCell<Object, Object> node = new TextFieldTreeTableCell<>();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof  StringConverter){
                node.setConverter((StringConverter) obj);
            }
        }

        return node;
    }
}
