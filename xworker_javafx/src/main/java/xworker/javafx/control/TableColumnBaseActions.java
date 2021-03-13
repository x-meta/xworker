package xworker.javafx.control;

import java.util.Comparator;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableColumnBase;
import xworker.javafx.util.JavaFXUtils;

public class TableColumnBaseActions {
	public static void init(TableColumnBase<Object, Object> node, Thing thing, ActionContext actionContext) {
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
        	Node graphic = JavaFXUtils.getGraphic(thing, "graphic", actionContext);
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
	}
}
