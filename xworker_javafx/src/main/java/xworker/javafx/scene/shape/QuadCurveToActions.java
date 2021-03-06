package xworker.javafx.scene.shape;

import javafx.scene.shape.QuadCurveTo;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class QuadCurveToActions {
    public static void init(QuadCurveTo node, Thing thing, ActionContext actionContext){
        PathElementActions.init(node, thing, actionContext);

        if(thing.valueExists("x")){
            node.setX(thing.getDouble("x"));
        }
        if(thing.valueExists("y")){
            node.setY(thing.getDouble("y"));
        }
        if(thing.valueExists("controlX")){
            node.setControlX(thing.getDouble("controlX"));
        }
        if(thing.valueExists("controlY")){
            node.setControlY(thing.getDouble("controlY"));
        }
    }

    public static QuadCurveTo create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        QuadCurveTo node = new QuadCurveTo();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }
}
