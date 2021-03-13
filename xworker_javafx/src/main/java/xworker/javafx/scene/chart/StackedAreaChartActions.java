package xworker.javafx.scene.chart;

import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.Axis;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class StackedAreaChartActions {
    public static void init(StackedAreaChart node, Thing thing, ActionContext actionContext){
        XYChartActions.init(node, thing, actionContext);

        if(thing.valueExists("createSymbols")){
            node.setCreateSymbols(thing.getBoolean("createSymbols"));
        }
    }

    public static StackedAreaChart create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Axis<?> xAxis = XYChartActions.getXAxis(self, actionContext);
        Axis<?> yAxis = XYChartActions.getYAxis(self, actionContext);
        StackedAreaChart node = new StackedAreaChart(xAxis, yAxis);
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }
}
