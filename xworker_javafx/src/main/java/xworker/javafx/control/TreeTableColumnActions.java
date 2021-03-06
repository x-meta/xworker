package xworker.javafx.control;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;

import java.util.Comparator;

public class TreeTableColumnActions {
    public static void init(TreeTableColumn<Object, Object> node, Thing thing, ActionContext actionContext){
        if(thing.valueExists("comparator")){
            Comparator<Object> comparator = JavaFXUtils.getObject(thing, "comparator", actionContext);
            if(comparator != null) {
                node.setComparator(comparator);
            }
        }
        if(thing.valueExists("contextMenu")){
            ContextMenu contextMenu = JavaFXUtils.getObject(thing, "contextMenu", actionContext);
            if(contextMenu != null) {
                node.setContextMenu(contextMenu);
            }
        }
        if(thing.valueExists("editable")){
            node.setEditable(thing.getBoolean("editable"));
        }
        if(thing.valueExists("graphic")){
            Node graphic = JavaFXUtils.getObject(thing, "graphic", actionContext);
            if(graphic != null) {
                node.setGraphic(graphic);
            }
        }
        if(thing.valueExists("id")){
            node.setId(thing.getString("id"));
        }
        if(thing.valueExists("maxWidth")){
            node.setMaxWidth(thing.getDouble("maxWidth"));
        }
        if(thing.valueExists("minWidth")){
            node.setMinWidth(thing.getDouble("minWidth"));
        }
        if(thing.valueExists("prefWidth")){
            node.setPrefWidth(thing.getDouble("prefWidth"));
        }
        if(thing.valueExists("resizable")){
            node.setResizable(thing.getBoolean("resizable"));
        }
        if(thing.valueExists("sortable")){
            node.setSortable(thing.getBoolean("sortable"));
        }
        if(thing.valueExists("sortNode")){
            Node sortNode = JavaFXUtils.getObject(thing, "sortNode", actionContext);
            if(sortNode != null) {
                node.setSortNode(sortNode);
            }
        }
        if(thing.valueExists("text")){
            node.setText(thing.getString("text"));
        }
        if(thing.valueExists("visible")){
            node.setVisible(thing.getBoolean("visible"));
        }
        if(thing.valueExists("style")){
            node.setStyle(thing.getString("style"));
        }
        if(thing.valueExists("cellFactory")){
            Callback<TreeTableColumn<Object,Object>, TreeTableCell<Object,Object>> cellFactory = JavaFXUtils.getObject(thing, "cellFactory", actionContext);
            if(cellFactory != null) {
                node.setCellFactory(cellFactory);
            }
        }
        if(thing.valueExists("cellValueFactory")){
            Callback<TreeTableColumn.CellDataFeatures<Object,Object>, ObservableValue<Object>> cellValueFactory = JavaFXUtils.getObject(thing, "cellValueFactory", actionContext);
            if(cellValueFactory != null) {
                node.setCellValueFactory(cellValueFactory);
            }
        }
        if(thing.valueExists("sortType")){
            node.setSortType(TreeTableColumn.SortType.valueOf(thing.getString("sortType")));
        }
    }

    public static TreeTableColumn<Object, Object> create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        TreeTableColumn<Object, Object> node = new TreeTableColumn<Object, Object>();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
        }

        return node;
    }
}
