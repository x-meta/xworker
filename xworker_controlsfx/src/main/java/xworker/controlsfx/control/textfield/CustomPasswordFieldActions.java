package xworker.controlsfx.control.textfield;

import javafx.scene.Node;
import org.controlsfx.control.textfield.CustomPasswordField;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.control.TextFieldActions;
import xworker.javafx.util.JavaFXUtils;

public class CustomPasswordFieldActions {
    public static void init(CustomPasswordField node, Thing thing, ActionContext actionContext){
        TextFieldActions.init(node, thing, actionContext);

        if(thing.valueExists("left")){
            Node leftNode = JavaFXUtils.getObject(thing, "left", actionContext);
            if(leftNode != null){
                node.setLeft(leftNode);
            }
        }

        if(thing.valueExists("right")){
            Node rightNode = JavaFXUtils.getObject(thing, "right", actionContext);
            if(rightNode != null){
                node.setRight(rightNode);
            }
        }
    }

    public static CustomPasswordField create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        CustomPasswordField node = new CustomPasswordField();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }

    public static void createLeft(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        CustomPasswordField parent = actionContext.getObject("parent");

        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof Node){
                parent.setLeft((Node) obj);
                return;
            }
        }
    }

    public static void createRight(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        CustomPasswordField parent = actionContext.getObject("parent");

        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof Node){
                parent.setRight((Node) obj);
                return;
            }
        }
    }
}
