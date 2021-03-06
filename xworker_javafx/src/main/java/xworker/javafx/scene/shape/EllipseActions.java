package xworker.javafx.scene.shape;

import javafx.scene.shape.Ellipse;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class EllipseActions {
    public static void init(Ellipse node, Thing thing, ActionContext actionContext){
        ShapeActions.init(node, thing, actionContext);

        if(thing.valueExists("centerX")){
            node.setCenterX(thing.getDouble("centerX"));
        }
        if(thing.valueExists("centerY")){
            node.setCenterY(thing.getDouble("centerY"));
        }
        if(thing.valueExists("radiusX")){
            node.setRadiusX(thing.getDouble("radiusX"));
        }
        if(thing.valueExists("radiusY")){
            node.setRadiusY(thing.getDouble("radiusY"));
        }
    }

    public static Ellipse create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Ellipse node = new Ellipse();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }
}
