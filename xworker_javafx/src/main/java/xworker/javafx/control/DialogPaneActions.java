package xworker.javafx.control;

import javafx.scene.Node;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;

public class DialogPaneActions {
    public static void init(DialogPane node, Thing thing, ActionContext actionContext){
        if(thing.valueExists("content")){
            Node content = JavaFXUtils.getObject(thing, "content", actionContext);
            if(content != null) {
                node.setContent(content);
            }
        }
        if(thing.valueExists("contentText")){
            String contentText = JavaFXUtils.getString(thing, "contentText", actionContext);
            if(contentText != null) {
                node.setContentText(contentText);
            }
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
            String headerText= JavaFXUtils.getString(thing, "headerText", actionContext);
            if(headerText != null) {
                node.setHeaderText(headerText);
            }
        }
    }

    public static DialogPane create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        DialogPane node = new DialogPane();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        Object parent = actionContext.getObject("parent");
        if(parent instanceof Dialog){
            ((Dialog<Object>) parent).setDialogPane(node);
        }
        return node;
    }

    public static void createContent(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Object parent = actionContext.get("parent");

        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof Node && parent instanceof DialogPane){
                ((DialogPane) parent).setContent((Node) obj);
            }
        }
    }

    public static void createExpandableContent(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Object parent = actionContext.get("parent");

        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof Node && parent instanceof DialogPane){
                ((DialogPane) parent).setExpandableContent((Node) obj);
            }
        }
    }

    public static void createGraphic(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Object parent = actionContext.get("parent");

        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof Node && parent instanceof DialogPane){
                ((DialogPane) parent).setGraphic((Node) obj);
            }
        }
    }

    public static void createHeader(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Object parent = actionContext.get("parent");

        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof Node && parent instanceof DialogPane){
                ((DialogPane) parent).setHeader((Node) obj);
            }
        }
    }
}
