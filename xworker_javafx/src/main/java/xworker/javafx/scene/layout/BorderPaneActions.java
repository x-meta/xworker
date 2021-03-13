package xworker.javafx.scene.layout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;

public class BorderPaneActions {
    public static void init(BorderPane node, Thing thing, ActionContext actionContext){
        PaneActions.init(node, thing, actionContext);
    }

    public static BorderPane create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        BorderPane node = new BorderPane();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof Node){
                node.getChildren().add((Node) obj);
            }
        }

        return node;
    }

    public static void createBottom(ActionContext actionContext){
        BorderPane parent = actionContext.getObject("parent");

        Thing self = actionContext.getObject("self");
        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof Node){
                parent.setBottom((Node) obj);
            }
        }
    }

    public static void createCenter(ActionContext actionContext){
        BorderPane parent = actionContext.getObject("parent");

        Thing self = actionContext.getObject("self");
        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof Node){
                parent.setCenter((Node) obj);
            }
        }
    }

    public static void createLeft(ActionContext actionContext){
        BorderPane parent = actionContext.getObject("parent");

        Thing self = actionContext.getObject("self");
        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof Node){
                parent.setLeft((Node) obj);
            }
        }
    }

    public static void createRight(ActionContext actionContext){
        BorderPane parent = actionContext.getObject("parent");

        Thing self = actionContext.getObject("self");
        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof Node){
                parent.setRight((Node) obj);
            }
        }
    }

    public static void createTop(ActionContext actionContext){
        BorderPane parent = actionContext.getObject("parent");

        Thing self = actionContext.getObject("self");
        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof Node){
                parent.setTop((Node) obj);
            }
        }
    }

    public static void createConstraints(ActionContext actionContext){
        Thing thing = actionContext.getObject("self");
        Node node = actionContext.getObject("parent");

        if(thing.valueExists("alignment")){
            BorderPane.setAlignment(node, Pos.valueOf(thing.getString("alignment")));
        }

        if(thing.valueExists("margin")){
            Insets margin = JavaFXUtils.getInsets(thing, "margin", actionContext);
            if(margin != null){
                BorderPane.setMargin(node, margin);
            }
        }
    }
}
