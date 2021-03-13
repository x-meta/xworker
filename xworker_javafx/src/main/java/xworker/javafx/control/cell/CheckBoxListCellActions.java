package xworker.javafx.control.cell;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.control.IndexedCellActions;
import xworker.javafx.control.ListCellActions;
import xworker.javafx.util.JavaFXUtils;

public class CheckBoxListCellActions {
    public static void init(CheckBoxListCell<Object> node, Thing thing, ActionContext actionContext){
        ListCellActions.init(node, thing, actionContext);

        if(thing.valueExists("converter")){
            StringConverter<Object> converter = JavaFXUtils.getObject(thing, "converter", actionContext);
            if(converter != null) {
                node.setConverter(converter);
            }
        }
        if(thing.valueExists("selectedStateCallback")){
            Callback<Object,ObservableValue<Boolean>> selectedStateCallback = JavaFXUtils.getObject(thing, "selectedStateCallback", actionContext);
            if(selectedStateCallback != null) {
                node.setSelectedStateCallback(selectedStateCallback);
            }
        }
    }

    public static CheckBoxListCell<Object> create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        CheckBoxListCell<Object> node = new CheckBoxListCell<>();
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
        CheckBoxListCell<Object> node = actionContext.getObject("parent");

        SelectedStateCallback callback = new SelectedStateCallback(self, actionContext);
        node.setSelectedStateCallback(callback);
    }

    public static class SelectedStateCallback implements Callback<Object,ObservableValue<Boolean>>{
        Thing thing;
        ActionContext actionContext;

        public SelectedStateCallback(Thing thing, ActionContext actionContext){
            this.thing = thing;
            this.actionContext = actionContext;
        }

        @Override
        public ObservableValue<Boolean> call(Object param) {
            return thing.doAction("call", actionContext, "param", param);
        }
    }
}
