package xworker.javafx.scene.layout;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;

public class AnchorPaneActions {
    public static void init(AnchorPane node, Thing thing, ActionContext actionContext){
        PaneActions.init(node, thing, actionContext);
    }

    public static AnchorPane create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        AnchorPane node = new AnchorPane();
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

    public static void createAnchor(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        String type = self.getStringBlankAsNull("type");
        Node node = JavaFXUtils.getObject(self, "node", actionContext);
        if(node == null){
            return;
        }
        double value = self.getDouble("value");
        if("BottomAnchor".equals(type)){
            AnchorPane.setBottomAnchor(node, value);
        }else if("LeftAnchor".equals(type)){
            AnchorPane.setLeftAnchor(node, value);
        }else if("RightAnchor".equals(type)){
            AnchorPane.setRightAnchor(node, value);
        }else if("TopAnchor".equals(type)){
            AnchorPane.setTopAnchor(node, value);
        }

    }

    public static void createConstraints(ActionContext actionContext){
        Thing thing = actionContext.getObject("self");
        Node node = actionContext.getObject("parent");

        String type = thing.getStringBlankAsNull("type");
        double value = thing.getDouble("value");

        if("BottomAnchor".equals(type)){
            AnchorPane.setBottomAnchor(node, value);
        }else if("LeftAnchor".equals(type)){
            AnchorPane.setLeftAnchor(node, value);
        }else if("RightAnchor".equals(type)){
            AnchorPane.setRightAnchor(node, value);
        }else if("TopAnchor".equals(type)){
            AnchorPane.setTopAnchor(node, value);
        }
    }
}
