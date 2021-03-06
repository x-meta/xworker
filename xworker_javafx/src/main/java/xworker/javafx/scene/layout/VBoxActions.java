package xworker.javafx.scene.layout;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;

public class VBoxActions {
    public static void init(VBox node, Thing thing, ActionContext actionContext){
        PaneActions.init(node, thing, actionContext);

        if(thing.valueExists("alignment")){
            node.setAlignment(Pos.valueOf(thing.getString("alignment")));
        }
        if(thing.valueExists("fillWidth")){
            node.setFillWidth(thing.getBoolean("fillWidth"));
        }
        if(thing.valueExists("spacing")){
            node.setSpacing(thing.getDouble("spacing"));
        }
    }

    public static VBox create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        VBox node = new VBox();
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

        if(thing.valueExists("vgrow")){
            VBox.setVgrow(node, Priority.valueOf(thing.getString("vgrow")));
        }

        if(thing.valueExists("margin")){
            Insets margin = JavaFXUtils.getInsets(thing, "margin", actionContext);
            if(margin != null){
                VBox.setMargin(node, margin);
            }
        }
    }
}
