package xworker.javafx.control.cell;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.control.TreeCellActions;
import xworker.javafx.util.JavaFXUtils;

public class CheckBoxTreeCellActions {
    public static void init(CheckBoxTreeCell<Object> node, Thing thing, ActionContext actionContext){
        TreeCellActions.init(node, thing, actionContext);

        if(thing.valueExists("converter")){
            StringConverter<TreeItem<Object>> converter = JavaFXUtils.getObject(thing, "converter", actionContext);
            if(converter != null) {
                node.setConverter(converter);
            }
        }
        if(thing.valueExists("selectedStateCallback")){
            Callback<TreeItem<Object>,ObservableValue<Boolean>> selectedStateCallback = JavaFXUtils.getObject(thing, "selectedStateCallback", actionContext);
            if(selectedStateCallback != null) {
                node.setSelectedStateCallback(selectedStateCallback);
            }
        }
    }

    public static CheckBoxTreeCell<Object> create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        CheckBoxTreeCell<Object> node = new CheckBoxTreeCell<>();
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
        CheckBoxTreeCell<Object> node = actionContext.getObject("parent");

        CheckBoxTreeCellActions.SelectedStateCallback callback = new CheckBoxTreeCellActions.SelectedStateCallback(self, actionContext);
        node.setSelectedStateCallback(callback);
    }

    public static class SelectedStateCallback implements Callback<TreeItem<Object>,ObservableValue<Boolean>>{
        Thing thing;
        ActionContext actionContext;

        public SelectedStateCallback(Thing thing, ActionContext actionContext){
            this.thing = thing;
            this.actionContext = actionContext;
        }


        @Override
        public ObservableValue<Boolean> call(TreeItem<Object> param) {
            return thing.doAction("call", actionContext, "param", param);
        }
    }
}
