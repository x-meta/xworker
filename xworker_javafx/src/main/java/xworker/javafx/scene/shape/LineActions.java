package xworker.javafx.scene.shape;

import javafx.scene.shape.Line;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class LineActions {
    public static void init(Line node, Thing thing, ActionContext actionContext){
        ShapeActions.init(node, thing, actionContext);

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

    public static Line create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Line node = new Line();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }
}
