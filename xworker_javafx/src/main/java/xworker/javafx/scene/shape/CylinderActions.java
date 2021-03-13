package xworker.javafx.scene.shape;

import javafx.scene.shape.Cylinder;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class CylinderActions {
    public static void init(Cylinder node, Thing thing, ActionContext actionContext){
        Shape3DActions.init(node, thing, actionContext);

        if(thing.valueExists("radius")){
            node.setRadius(thing.getDouble("radius"));
        }
        if(thing.valueExists("height")){
            node.setHeight(thing.getDouble("height"));
        }
    }

    public static Cylinder create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Cylinder node = new Cylinder();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }
}
