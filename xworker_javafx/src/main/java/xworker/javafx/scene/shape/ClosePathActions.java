package xworker.javafx.scene.shape;

import javafx.scene.shape.ClosePath;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ClosePathActions {
    public static void init(ClosePath node, Thing thing, ActionContext actionContext){
        PathElementActions.init(node, thing, actionContext);
    }

    public static ClosePath create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        ClosePath node = new ClosePath();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }
}
