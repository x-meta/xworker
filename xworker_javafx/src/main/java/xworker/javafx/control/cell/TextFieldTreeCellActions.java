package xworker.javafx.control.cell;

import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.util.StringConverter;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.control.TreeCellActions;
import xworker.javafx.util.JavaFXUtils;

public class TextFieldTreeCellActions {
    public static void init(TextFieldTreeCell<Object> node, Thing thing, ActionContext actionContext){
        TreeCellActions.init(node, thing, actionContext);

        if(thing.valueExists("converter")){
            StringConverter<Object> converter = JavaFXUtils.getObject(thing, "converter", actionContext);
            if(converter != null) {
                node.setConverter(converter);
            }
        }
    }

    public static TextFieldTreeCell<Object> create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        TextFieldTreeCell<Object> node = new TextFieldTreeCell<>();
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
