package xworker.javafx.scene.shape;

import javafx.scene.shape.CubicCurveTo;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class CubicCurveToActions {
    public static void init(CubicCurveTo node, Thing thing, ActionContext actionContext){
        PathElementActions.init(node, thing, actionContext);

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
        if(thing.valueExists("x")){
            node.setX(thing.getDouble("x"));
        }
        if(thing.valueExists("y")){
            node.setY(thing.getDouble("y"));
        }
    }

    public static CubicCurveTo create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        CubicCurveTo node = new CubicCurveTo();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }
}
