package xworker.javafx.control.cell;

import javafx.scene.control.cell.ChoiceBoxListCell;
import javafx.util.StringConverter;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.control.IndexedCellActions;
import xworker.javafx.control.ListCellActions;
import xworker.javafx.util.JavaFXUtils;

import java.util.Iterator;

public class ChoiceBoxListCellActions {
    public static void init(ChoiceBoxListCell<Object> node, Thing thing, ActionContext actionContext){
        ListCellActions.init(node, thing, actionContext);

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

    public static ChoiceBoxListCell<Object> create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        ChoiceBoxListCell<Object> node = new ChoiceBoxListCell<>();
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
