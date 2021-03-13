package xworker.javafx.control.cell;

import javafx.scene.control.cell.ChoiceBoxTreeTableCell;
import javafx.util.StringConverter;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.control.TableCellActions;
import xworker.javafx.control.TreeTableCellActions;
import xworker.javafx.util.JavaFXUtils;

import java.util.Iterator;

public class ChoiceBoxTreeTableCellActions {
    public static void init(ChoiceBoxTreeTableCell<Object, Object> node, Thing thing, ActionContext actionContext){
        TreeTableCellActions.init(node, thing, actionContext);

        if(thing.valueExists("converter")){
            StringConverter<Object> converter = JavaFXUtils.getObject(thing, "converter", actionContext);
            if(converter != null) {
                node.setConverter(converter);
            }
        }
        if(thing.valueExists("items")){
            Iterable<Object> items = JavaFXUtils.getObject(thing, "items", actionContext);

            if(items != null) {
                Iterator<Object> iter = items.iterator();
                while(iter.hasNext()){
                    node.getItems().add(iter.next());
                }
            }
        }
    }

    public static ChoiceBoxTreeTableCell<Object, Object> create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        ChoiceBoxTreeTableCell<Object, Object> node = new ChoiceBoxTreeTableCell<>();
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
