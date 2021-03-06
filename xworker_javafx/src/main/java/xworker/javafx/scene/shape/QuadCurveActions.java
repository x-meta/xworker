package xworker.javafx.scene.shape;

import javafx.scene.shape.QuadCurve;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class QuadCurveActions {
    public static void init(QuadCurve node, Thing thing, ActionContext actionContext){
        ShapeActions.init(node, thing, actionContext);

        if(thing.valueExists("controlX")){
            node.setControlX(thing.getDouble("controlX"));
        }
        if(thing.valueExists("controlY")){
            node.setControlY(thing.getDouble("controlY"));
        }
        if(thing.valueExists("endX")){
            node.setEndX(thing.getDouble("endX"));
        }
        if(thing.valueExists("endY")){
            node.setEndY(thing.getDouble("endY"));
        }
        if(thing.valueExists("startX")){
            node.setStartX(thing.getDouble("startX"));
        }
        if(thing.valueExists("startY")){
            node.setStartY(thing.getDouble("startY"));
        }
    }

    public static QuadCurve create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        QuadCurve node = new QuadCurve();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }
}
