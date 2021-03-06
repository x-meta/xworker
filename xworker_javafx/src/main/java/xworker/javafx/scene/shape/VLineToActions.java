package xworker.javafx.scene.shape;

import javafx.scene.shape.VLineTo;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class VLineToActions {
    public static void init(VLineTo node, Thing thing, ActionContext actionContext){
        PathElementActions.init(node, thing, actionContext);

        if(thing.valueExists("y")){
            node.setY(thing.getDouble("y"));
        }
    }

    public static VLineTo create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        VLineTo node = new VLineTo();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }
}
