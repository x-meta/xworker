package xworker.javafx.scene.shape;

import javafx.scene.shape.Polyline;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class PolylineActions {
    public static void init(Polyline node, Thing thing, ActionContext actionContext){
        ShapeActions.init(node, thing, actionContext);

        if(thing.valueExists("points")){
            String points = thing.getString("points").trim();
            for(String line : points.split("[\n]")){
                for(String p : line.split("[,]")){
                    node.getPoints().add(Double.parseDouble(p.trim()));
                }
            }
        }
    }

    public static Polyline create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Polyline node = new Polyline();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }
}
