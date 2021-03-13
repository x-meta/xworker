package xworker.javafx.scene.chart;

import javafx.scene.chart.NumberAxis;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class NumberAxisActions {
    public static void init(NumberAxis node, Thing thing, ActionContext actionContext){
        ValueAxisActions.init(node, thing, actionContext);

        if(thing.valueExists("forceZeroInRange")){
            node.setForceZeroInRange(thing.getBoolean("forceZeroInRange"));
        }
        if(thing.valueExists("tickUnit")){
            node.setTickUnit(thing.getDouble("tickUnit"));
        }
    }

    public static NumberAxis create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        NumberAxis node = new NumberAxis();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }
}
