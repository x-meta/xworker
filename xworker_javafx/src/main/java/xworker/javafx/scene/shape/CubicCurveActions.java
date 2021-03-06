package xworker.javafx.scene.shape;

import javafx.scene.shape.CubicCurve;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class CubicCurveActions {
    public static void init(CubicCurve node, Thing thing, ActionContext actionContext){
        ShapeActions.init(node, thing, actionContext);

        if(thing.valueExists("controlX1")){
            node.setControlX1(thing.getDouble("controlX1"));
        }
        if(thing.valueExists("controlX2")){
            node.setControlX2(thing.getDouble("controlX2"));
        }
        if(thing.valueExists("controlY1")){
            node.setControlY1(thing.getDouble("controlY1"));
        }
        if(thing.valueExists("controlY2")){
            node.setControlY2(thing.getDouble("controlY2"));
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

    public static CubicCurve create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        CubicCurve node = new CubicCurve();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }
}
