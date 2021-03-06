package xworker.javafx.scene.shape;

import javafx.scene.shape.LineTo;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class LineToActions {
    public static void init(LineTo node, Thing thing, ActionContext actionContext){
        PathElementActions.init(node, thing, actionContext);

        if(thing.valueExists("x")){
            node.setX(thing.getDouble("x"));
        }
        if(thing.valueExists("y")){
            node.setY(thing.getDouble("y"));
        }
    }

    public static LineTo create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        LineTo node = new LineTo();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }
}
