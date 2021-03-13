package xworker.javafx.scene.shape;

import javafx.scene.shape.Box;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class BoxActions {
    public static void init(Box node, Thing thing, ActionContext actionContext){
        Shape3DActions.init(node, thing, actionContext);

        if(thing.valueExists("depth")){
            node.setDepth(thing.getDouble("depth"));
        }
        if(thing.valueExists("height")){
            node.setHeight(thing.getDouble("height"));
        }
        if(thing.valueExists("width")){
            node.setWidth(thing.getDouble("width"));
        }
    }

    public static Box create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Box node = new Box();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }
}
