package xworker.javafx.control;

import javafx.scene.control.*;
import javafx.util.Callback;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;

public class TreeViewActions {
    public static void init(TreeView<Object> node, Thing thing, ActionContext actionContext){
        ControlActions.init(node, thing, actionContext);

        if(thing.valueExists("cellFactory")){
            Callback<TreeView<Object>, TreeCell<Object>> cellFactory = JavaFXUtils.getObject(thing, "cellFactory", actionContext);
            if(cellFactory != null) {
                node.setCellFactory(cellFactory);
            }
        }
        if(thing.valueExists("editable")){
            node.setEditable(thing.getBoolean("editable"));
        }
        if(thing.valueExists("fixedCellSize")){
            node.setFixedCellSize(thing.getDouble("fixedCellSize"));
        }
        if(thing.valueExists("focusModel")){
            FocusModel<TreeItem<Object>> focusModel = JavaFXUtils.getObject(thing, "focusModel", actionContext);
            if(focusModel != null) {
                node.setFocusModel(focusModel);
            }
        }
        if(thing.valueExists("root")){
            TreeItem<Object> root = JavaFXUtils.getObject(thing, "root", actionContext);
            if(root != null) {
                node.setRoot(root);
            }
        }
        if(thing.valueExists("selectionModel")){
            MultipleSelectionModel<TreeItem<Object>> selectionModel = JavaFXUtils.getObject(thing, "selectionModel", actionContext);
            if(selectionModel != null) {
                node.setSelectionModel(selectionModel);
            }
        }
        if(thing.valueExists("showRoot")){
            node.setShowRoot(thing.getBoolean("showRoot"));
        }
    }

    public static TreeView<Object> create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        TreeView<Object> node = new TreeView<Object>();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()) {
            Object obj = child.doAction("create", actionContext);
            if (obj instanceof TreeItem) {
                node.setRoot((TreeItem<Object>) obj);
            }
        }

        return node;
    }
}
