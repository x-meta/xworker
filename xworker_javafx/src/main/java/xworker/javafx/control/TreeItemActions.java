package xworker.javafx.control;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;

public class TreeItemActions {
    public static void init(TreeItem<Object> node, Thing thing, ActionContext actionContext){
        if(thing.valueExists("expanded")){
            node.setExpanded(thing.getBoolean("expanded"));
        }
        if(thing.valueExists("graphic")){
            Node graphic = JavaFXUtils.getGraphic(thing, "graphic", actionContext);
            if(graphic != null) {
                node.setGraphic(graphic);
            }
        }
        if(thing.valueExists("value")){
            Object value = JavaFXUtils.getObject(thing, "value", actionContext);
            node.setValue(value);
        }
    }

    public static TreeItem<Object> create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        TreeItem<Object> node = new TreeItem<Object>();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof TreeItem){
                node.getChildren().add((TreeItem<Object>) obj);
            }
        }

        return node;
    }
}
