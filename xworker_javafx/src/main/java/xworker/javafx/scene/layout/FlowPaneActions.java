package xworker.javafx.scene.layout;

import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;

public class FlowPaneActions {
    public static void init(FlowPane node, Thing thing, ActionContext actionContext){
        PaneActions.init(node, thing, actionContext);

        if(thing.valueExists("alignment")){
            node.setAlignment(Pos.valueOf(thing.getString("alignment")));
        }
        if(thing.valueExists("columnHalignment")){
            node.setColumnHalignment(HPos.valueOf(thing.getString("columnHalignment")));
        }
        if(thing.valueExists("hgap")){
            node.setHgap(thing.getDouble("hgap"));
        }
        if(thing.valueExists("orientation")){
            node.setOrientation(Orientation.valueOf(thing.getString("orientation")));
        }
        if(thing.valueExists("prefWrapLength")){
            node.setPrefWrapLength(thing.getDouble("prefWrapLength"));
        }
        if(thing.valueExists("rowValignment")){
            node.setRowValignment(VPos.valueOf(thing.getString("rowValignment")));
        }
        if(thing.valueExists("vgap")){
            node.setVgap(thing.getDouble("vgap"));
        }
    }

    public static FlowPane create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        FlowPane node = new FlowPane();
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

        if(thing.valueExists("margin")){
            Insets margin = JavaFXUtils.getInsets(thing,"margin", actionContext);
            if(margin != null){
                FlowPane.setMargin(node, margin);
            }
        }
    }
}
