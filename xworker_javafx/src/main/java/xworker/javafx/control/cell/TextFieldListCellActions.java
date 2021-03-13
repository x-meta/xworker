package xworker.javafx.control.cell;

import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.StringConverter;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.control.IndexedCellActions;
import xworker.javafx.control.ListCellActions;
import xworker.javafx.util.JavaFXUtils;

import java.util.Iterator;

public class TextFieldListCellActions {
    public static void init(TextFieldListCell<Object> node, Thing thing, ActionContext actionContext){
        ListCellActions.init(node, thing, actionContext);

        if(thing.valueExists("converter")){
            StringConverter<Object> converter = JavaFXUtils.getObject(thing, "converter", actionContext);
            if(converter != null) {
                node.setConverter(converter);
            }
        }
    }

    public static TextFieldListCell<Object> create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        TextFieldListCell<Object> node = new TextFieldListCell<>();
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
