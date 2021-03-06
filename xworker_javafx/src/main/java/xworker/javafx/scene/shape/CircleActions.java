package xworker.javafx.scene.shape;

import javafx.scene.shape.Circle;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class CircleActions {
    public static void init(Circle node, Thing thing, ActionContext actionContext){
        ShapeActions.init(node, thing, actionContext);

        if(thing.valueExists("centerX")){
            node.setCenterX(thing.getDouble("centerX"));
        }
        if(thing.valueExists("centerY")){
            node.setCenterY(thing.getDouble("centerY"));
        }
        if(thing.valueExists("radius")){
            node.setRadius(thing.getDouble("radius"));
        }
    }

    public static Circle create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Circle node = new Circle();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }
}
