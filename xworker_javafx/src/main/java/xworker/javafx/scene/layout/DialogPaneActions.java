package xworker.javafx.scene.layout;

import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;

public class DialogPaneActions {
    public static void init(DialogPane node, Thing thing, ActionContext actionContext){
        PaneActions.init(node, thing, actionContext);

        if(thing.valueExists("content")){
            Node content = JavaFXUtils.getObject(thing, "content", actionContext);
            if(content != null) {
                node.setContent(content);
            }
        }
        if(thing.valueExists("contentText")){
            node.setContentText(thing.getString("contentText"));
        }
        if(thing.valueExists("expandableContent")){
            Node expandableContent = JavaFXUtils.getObject(thing, "expandableContent", actionContext);
            if(expandableContent != null) {
                node.setExpandableContent(expandableContent);
            }
        }
        if(thing.valueExists("expanded")){
            node.setExpanded(thing.getBoolean("expanded"));
        }
        if(thing.valueExists("graphic")){
            Node graphic = JavaFXUtils.getObject(thing, "graphic", actionContext);
            if(graphic != null) {
                node.setGraphic(graphic);
            }
        }
        if(thing.valueExists("header")){
            Node header = JavaFXUtils.getObject(thing, "header", actionContext);
            if(header != null) {
                node.setHeader(header);
            }
        }
        if(thing.valueExists("headerText")){
            node.setHeaderText(thing.getString("headerText"));
        }
    }

    public static DialogPane create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        DialogPane node = new DialogPane();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof Node){
                node.getChildren().add((Node) obj);
            }else if(obj instanceof ButtonType){
                node.getButtonTypes().add((ButtonType) obj);
            }
        }

        return node;
    }

    public static void createContent(ActionContext actionContext){
        DialogPane parent = actionContext.getObject("parent");

        Thing self = actionContext.getObject("self");
        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof Node){
                parent.setContent((Node) obj);
            }
        }
    }

    public static void createExpandableContent(ActionContext actionContext){
        DialogPane parent = actionContext.getObject("parent");

        Thing self = actionContext.getObject("self");
        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof Node){
                parent.setExpandableContent((Node) obj);
            }
        }
    }

    public static void createGraphic(ActionContext actionContext){
        DialogPane parent = actionContext.getObject("parent");

        Thing self = actionContext.getObject("self");
        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof Node){
                parent.setGraphic((Node) obj);
            }
        }
    }

    public static void createHeader(ActionContext actionContext){
        DialogPane parent = actionContext.getObject("parent");

        Thing self = actionContext.getObject("self");
        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof Node){
                parent.setHeader((Node) obj);
            }
        }
    }


}
