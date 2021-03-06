package xworker.javafx.scene.canvas;

import javafx.scene.canvas.Canvas;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.scene.NodeActions;

public class CanvasActions {
    public static void init(Canvas node, Thing thing, ActionContext actionContext){
        NodeActions.init(node, thing, actionContext);

        if(thing.valueExists("height")){
            node.setHeight(thing.getDouble("height"));
        }

        if(thing.valueExists("width")){
            node.setWidth(thing.getDouble("width"));
        }
    }

    public static Canvas create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Canvas node = new Canvas();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }
}
