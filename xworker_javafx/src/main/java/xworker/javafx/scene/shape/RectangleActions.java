package xworker.javafx.scene.shape;

import javafx.scene.shape.Rectangle;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class RectangleActions {
    public static void init(Rectangle node, Thing thing, ActionContext actionContext){
        ShapeActions.init(node, thing, actionContext);

        if(thing.valueExists("arcHeight")){
            node.setArcHeight(thing.getDouble("arcHeight"));
        }
        if(thing.valueExists("arcWidth")){
            node.setArcWidth(thing.getDouble("arcWidth"));
        }
        if(thing.valueExists("height")){
            node.setHeight(thing.getDouble("height"));
        }
        if(thing.valueExists("width")){
            node.setWidth(thing.getDouble("width"));
        }
        if(thing.valueExists("x")){
            node.setX(thing.getDouble("x"));
        }
        if(thing.valueExists("y")){
            node.setY(thing.getDouble("y"));
        }
    }

    public static Rectangle create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Rectangle node = new Rectangle();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }
}
