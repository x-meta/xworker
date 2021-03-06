package xworker.javafx.scene.layout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;

public class StackPaneActions {
    public static void init(StackPane node, Thing thing, ActionContext actionContext){
        PaneActions.init(node, thing, actionContext);

        if(thing.valueExists("alignment")){
            node.setAlignment(Pos.valueOf(thing.getString("alignment")));
        }
    }

    public static StackPane create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        StackPane node = new StackPane();
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

    public static void createConstraints(ActionContext actionContext){
        Thing thing = actionContext.getObject("self");
        Node node = actionContext.getObject("parent");

        if(thing.valueExists("alignment")){
            StackPane.setAlignment(node, Pos.valueOf(thing.getString("alignment")));
        }

        if(thing.valueExists("margin")){
            Insets margin = JavaFXUtils.getInsets(thing, "margin", actionContext);
            if(margin != null){
                StackPane.setMargin(node, margin);
            }
        }
    }
}
