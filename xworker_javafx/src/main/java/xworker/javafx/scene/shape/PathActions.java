package xworker.javafx.scene.shape;

import javafx.scene.shape.FillRule;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class PathActions {
    public static void init(Path node, Thing thing, ActionContext actionContext){
        ShapeActions.init(node, thing, actionContext);

        if(thing.valueExists("fillRule")){
            node.setFillRule(FillRule.valueOf(thing.getString("fillRule")));
        }
    }

    public static Path create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Path node = new Path();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof PathElement){
                node.getElements().add((PathElement) obj);
            }
        }

        return node;
    }
}
