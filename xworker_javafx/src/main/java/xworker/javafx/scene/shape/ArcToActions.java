package xworker.javafx.scene.shape;

import javafx.scene.shape.ArcTo;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ArcToActions {
    public static void init(ArcTo node, Thing thing, ActionContext actionContext){
        PathElementActions.init(node, thing, actionContext);

        if(thing.valueExists("largeArcFlag")){
            node.setLargeArcFlag(thing.getBoolean("largeArcFlag"));
        }
        if(thing.valueExists("radiusX")){
            node.setRadiusX(thing.getDouble("radiusX"));
        }
        if(thing.valueExists("radiusY")){
            node.setRadiusY(thing.getDouble("radiusY"));
        }
        if(thing.valueExists("sweepFlag")){
            node.setSweepFlag(thing.getBoolean("sweepFlag"));
        }
        if(thing.valueExists("XAxisRotation")){
            node.setXAxisRotation(thing.getDouble("XAxisRotation"));
        }
        if(thing.valueExists("x")){
            node.setX(thing.getDouble("x"));
        }
        if(thing.valueExists("y")){
            node.setY(thing.getDouble("y"));
        }

    }

    public static ArcTo create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        ArcTo node = new ArcTo();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }
}
