package xworker.javafx.control.cell;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.control.TreeTableCellActions;
import xworker.javafx.util.JavaFXUtils;

public class CheckBoxTreeTableCellActions {
    public static void init(CheckBoxTreeTableCell<Object, Object> node, Thing thing, ActionContext actionContext){
        TreeTableCellActions.init(node, thing, actionContext);

        if(thing.valueExists("converter")){
            StringConverter<Object> converter = JavaFXUtils.getObject(thing, "converter", actionContext);
            if(converter != null) {
                node.setConverter(converter);
            }
        }
        if(thing.valueExists("selectedStateCallback")){
            Callback<Integer, ObservableValue<Boolean>> selectedStateCallback = JavaFXUtils.getObject(thing, "selectedStateCallback", actionContext);
            if(selectedStateCallback != null) {
                node.setSelectedStateCallback(selectedStateCallback);
            }
        }
    }

    public static CheckBoxTreeTableCell<Object, Object> create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        CheckBoxTreeTableCell<Object, Object> node = new CheckBoxTreeTableCell<>();
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

    public static void createSelectedStateCallback(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        CheckBoxTreeTableCell<Object, Object> node = actionContext.getObject("parent");

        CheckBoxTreeTableCellActions.SelectedStateCallback callback = new CheckBoxTreeTableCellActions.SelectedStateCallback(self, actionContext);
        node.setSelectedStateCallback(callback);
    }

    public static class SelectedStateCallback implements Callback<Integer,ObservableValue<Boolean>>{
        Thing thing;
        ActionContext actionContext;

        public SelectedStateCallback(Thing thing, ActionContext actionContext){
            this.thing = thing;
            this.actionContext = actionContext;
        }

        @Override
        public ObservableValue<Boolean> call(Integer param) {
            return thing.doAction("call", actionContext, "param", param);
        }
    }
}
