package xworker.javafx.control.cell;

import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.control.TableCellActions;
import xworker.javafx.util.JavaFXUtils;

public class TextFieldTableCellActions {
    public static void init(TextFieldTableCell<Object, Object> node, Thing thing, ActionContext actionContext){
        TableCellActions.init(node, thing, actionContext);

        if(thing.valueExists("converter")){
            StringConverter<Object> converter = JavaFXUtils.getObject(thing, "converter", actionContext);
            if(converter != null) {
                node.setConverter(converter);
            }
        }
    }

    public static TextFieldTableCell<Object, Object> create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        TextFieldTableCell<Object, Object> node = new TextFieldTableCell<>();
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
