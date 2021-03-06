package xworker.javafx.scene.shape;

import javafx.scene.shape.MoveTo;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class MoveToActions {
    public static void init(MoveTo node, Thing thing, ActionContext actionContext){
        PathElementActions.init(node, thing, actionContext);

        if(thing.valueExists("x")){
            node.setX(thing.getDouble("x"));
        }
        if(thing.valueExists("y")){
            node.setY(thing.getDouble("y"));
        }
    }

    public static MoveTo create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        MoveTo node = new MoveTo();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }
}
