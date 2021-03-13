package xworker.javafx.control;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;
import xworker.javafx.util.ThingCallback;

public class TreeTableViewActions {
    public static void init(TreeTableView<Object> node, Thing thing, ActionContext actionContext){
        ControlActions.init(node, thing, actionContext);

        if(thing.valueExists("columnResizePolicy")){
            Callback<TreeTableView.ResizeFeatures,Boolean> columnResizePolicy = JavaFXUtils.getObject(thing, "columnResizePolicy", actionContext);
            if(columnResizePolicy != null) {
                node.setColumnResizePolicy(columnResizePolicy);
            }
        }
        if(thing.valueExists("editable")){
            node.setEditable(thing.getBoolean("editable"));
        }
        if(thing.valueExists("fixedCellSize")){
            node.setFixedCellSize(thing.getDouble("fixedCellSize"));
        }
        if(thing.valueExists("focusModel")){
            TreeTableView.TreeTableViewFocusModel<Object> focusModel = JavaFXUtils.getObject(thing, "focusModel", actionContext);
            if(focusModel != null) {
                node.setFocusModel(focusModel);
            }
        }
        if(thing.valueExists("placeholder")){
            Node placeholder = JavaFXUtils.getObject(thing, "placeholder", actionContext);
            if(placeholder != null) {
                node.setPlaceholder(placeholder);
            }
        }
        if(thing.valueExists("root")){
            TreeItem<Object> root = JavaFXUtils.getObject(thing, "root", actionContext);
            if(root != null) {
                node.setRoot(root);
            }
        }
        if(thing.valueExists("rowFactory")){
            Callback<TreeTableView<Object>, TreeTableRow<Object>> rowFactory = JavaFXUtils.getObject(thing, "rowFactory", actionContext);
            if(rowFactory != null) {
                node.setRowFactory(rowFactory);
            }
        }
        if(thing.valueExists("selectionModel")){
            if("MULTIPLE".equals(thing.getString("selectionModel"))){
                node.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            }else{
                node.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            }
        }
        if(thing.valueExists("cellSelectionEnabled")){
            node.getSelectionModel().setCellSelectionEnabled(thing.getBoolean("cellSelectionEnabled"));
        }
        if(thing.valueExists("showRoot")){
            node.setShowRoot(thing.getBoolean("showRoot"));
        }
        if(thing.valueExists("sortMode")){
            node.setSortMode(TreeSortMode.valueOf(thing.getString("sortMode")));
        }
        if(thing.valueExists("sortPolicy")){
            Callback<TreeTableView<Object>,Boolean> sortPolicy = JavaFXUtils.getObject(thing, "sortPolicy", actionContext);
            if(sortPolicy != null) {
                node.setSortPolicy(sortPolicy);
            }
        }
        if(thing.valueExists("tableMenuButtonVisible")){
            node.setTableMenuButtonVisible(thing.getBoolean("tableMenuButtonVisible"));
        }
        if(thing.valueExists("treeColumn")){
            TreeTableColumn<Object,Object> treeColumn = JavaFXUtils.getObject(thing, "treeColumn", actionContext);
            if(treeColumn != null) {
                node.setTreeColumn(treeColumn);
            }
        }
    }

    public static TreeTableView<Object> create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        TreeTableView<Object> node = new TreeTableView<Object>();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof TreeItem){
                node.setRoot((TreeItem<Object>) obj);
            }else if(obj instanceof  TreeTableColumn){
                node.getColumns().add((TreeTableColumn<Object, Object>) obj);
            }
        }

        return node;
    }

    public static void createColumnResizePolicy(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        TreeTableView<?> parent = actionContext.getObject("parent");

        parent.setColumnResizePolicy(new ThingCallback<>(self, actionContext));
    }

    public static void createRowFactory(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        TreeTableView<?> parent = actionContext.getObject("parent");

        parent.setRowFactory(new ThingRowFactory<>(self, actionContext));
    }

    public static void createSortPolicy(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        TreeTableView<?> parent = actionContext.getObject("parent");

        parent.setSortPolicy(new ThingCallback<>(self, actionContext));
    }

    public static void createPlaceholder(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        TreeTableView<?> parent = actionContext.getObject("parent");


        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof Node){
                parent.setPlaceholder((Node) obj);
            }
        }
    }

    public static class ThingRowFactory<S> implements Callback<TreeTableView<S>,TreeTableRow<S>>{
        Thing thing;
        ActionContext actionContext;

        public ThingRowFactory(Thing thing, ActionContext actionContext){
            this.thing = thing;
            this.actionContext = actionContext;
        }

        @Override
        public TreeTableRow<S> call(TreeTableView<S> param) {
            TreeTableRow node  = new TreeTableRowActions.ThingTreeTableRow(thing, actionContext);
            TreeTableRowActions.init(node, thing, actionContext);

            actionContext.peek().put("parent", node);
            for(Thing child : thing.getChilds()){
                child.doAction("create", actionContext);
            }
            return node;
        }
    }
}
