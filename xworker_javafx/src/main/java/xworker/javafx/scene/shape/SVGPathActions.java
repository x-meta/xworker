package xworker.javafx.scene.shape;

import javafx.scene.shape.FillRule;
import javafx.scene.shape.SVGPath;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class SVGPathActions {
    public static void init(SVGPath node, Thing thing, ActionContext actionContext){
        ShapeActions.init(node, thing, actionContext);

        if(thing.valueExists("content")){
            node.setContent(thing.getString("content"));
        }
        if(thing.valueExists("fillRule")){
            node.setFillRule(FillRule.valueOf(thing.getString("fillRule")));
        }
    }

    public static SVGPath create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        SVGPath node = new SVGPath();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }
}
