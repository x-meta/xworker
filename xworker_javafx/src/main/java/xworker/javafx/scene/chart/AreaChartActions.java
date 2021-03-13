package xworker.javafx.scene.chart;

import javafx.scene.chart.AreaChart;
import javafx.scene.chart.Axis;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class AreaChartActions {
    public static void init(AreaChart node, Thing thing, ActionContext actionContext){
        XYChartActions.init(node, thing, actionContext);

        if(thing.valueExists("createSymbols")){
            node.setCreateSymbols(thing.getBoolean("createSymbols"));
        }
    }

    public static AreaChart create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Axis<?> xAxis = XYChartActions.getXAxis(self, actionContext);
        Axis<?> yAxis = XYChartActions.getYAxis(self, actionContext);
        AreaChart node = new AreaChart(xAxis, yAxis);
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }
}
