package xworker.javafx.control;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;
import xworker.javafx.util.ThingCallback;

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
            if("MULTIPLE".equals(thing.getString("selectionModel"))){
                node.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            }else{
                node.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
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

        node.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<Object>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<Object>> observable, TreeItem<Object> oldValue, TreeItem<Object> newValue) {

            }
        });

        return node;
    }

    public static void createCellFactory(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        TreeView<?> parent = actionContext.getObject("parent");

        parent.setCellFactory(new ThingCallback<>(self, actionContext));
    }

    public static Object defaultCreateCell(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        for(Thing  child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof TreeCell){
                return obj;
            }
        }

        return null;
    }

    public static void createSelectionChangeListener(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Object parent = actionContext.getObject("parent");
        if(parent instanceof TreeView){
            ((TreeView) parent).getSelectionModel().selectedItemProperty().addListener(new ThingChangeListener(self, actionContext));
        }else if(parent instanceof TableView){
            ((TableView) parent).getSelectionModel().selectedItemProperty().addListener(new ThingChangeListener(self, actionContext));
        }else if(parent instanceof TreeTableView){
            ((TreeTableView) parent).getSelectionModel().selectedItemProperty().addListener(new ThingChangeListener(self, actionContext));
        }else if(parent instanceof ListView){
            ((ListView) parent).getSelectionModel().selectedItemProperty().addListener(new ThingChangeListener(self, actionContext));
        }else if(parent instanceof ComboBox){
            ((ComboBox) parent).getSelectionModel().selectedItemProperty().addListener(new ThingChangeListener(self, actionContext));
        }else if(parent instanceof ChoiceBox){
            ((ChoiceBox) parent).getSelectionModel().selectedItemProperty().addListener(new ThingChangeListener(self, actionContext));
        }
    }

    public static class ThingChangeListener<T> implements  ChangeListener<T>{
        Thing thing;
        ActionContext actionContext;

        public ThingChangeListener(Thing thing, ActionContext actionContext){
            this.thing = thing;
            this.actionContext = actionContext;
        }

        @Override
        public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
            thing.doAction("changed", actionContext, "observable", observable,
                    "oldValue", oldValue, "newValue", newValue);
        }
    }
}
