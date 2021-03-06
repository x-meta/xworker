package xworker.javafx.scene.shape;

import javafx.scene.shape.HLineTo;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class HLineToActions {
    public static void init(HLineTo node, Thing thing, ActionContext actionContext){
        PathElementActions.init(node, thing, actionContext);

        if(thing.valueExists("x")){
            node.setX(thing.getDouble("x"));
        }
    }

    public static HLineTo create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        HLineTo node = new HLineTo();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }
}
