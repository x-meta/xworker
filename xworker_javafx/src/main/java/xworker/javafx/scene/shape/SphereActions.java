package xworker.javafx.scene.shape;

import javafx.scene.shape.Sphere;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class SphereActions {
    public static void init(Sphere node, Thing thing, ActionContext actionContext){
        Shape3DActions.init(node, thing, actionContext);

        if(thing.valueExists("radius")){
            node.setRadius(thing.getDouble("radius"));
        }
    }

    public static Sphere create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Sphere node = new Sphere();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }
}
