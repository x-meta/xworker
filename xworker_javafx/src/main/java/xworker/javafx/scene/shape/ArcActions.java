package xworker.javafx.scene.shape;

import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ArcActions {
    public static void init(Arc node, Thing thing, ActionContext actionContext){
        ShapeActions.init(node, thing, actionContext);

        if(thing.valueExists("centerX")){
            node.setCenterX(thing.getDouble("centerX"));
        }
        if(thing.valueExists("centerY")){
            node.setCenterY(thing.getDouble("centerY"));
        }
        if(thing.valueExists("length")){
            node.setLength(thing.getDouble("length"));
        }
        if(thing.valueExists("radiusX")){
            node.setRadiusX(thing.getDouble("radiusX"));
        }
        if(thing.valueExists("radiusY")){
            node.setRadiusY(thing.getDouble("radiusY"));
        }
        if(thing.valueExists("startAngle")){
            node.setStartAngle(thing.getDouble("startAngle"));
        }
        if(thing.valueExists("type")){
            node.setType(ArcType.valueOf(thing.getString("type")));
        }
    }

    public static Arc create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Arc node = new Arc();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }
}
