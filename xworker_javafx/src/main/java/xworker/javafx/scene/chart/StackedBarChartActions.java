package xworker.javafx.scene.chart;

import javafx.scene.chart.Axis;
import javafx.scene.chart.StackedBarChart;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class StackedBarChartActions {
    public static void init(StackedBarChart node, Thing thing, ActionContext actionContext){
        XYChartActions.init(node, thing, actionContext);

        if(thing.valueExists("categoryGap")){
            node.setCategoryGap(thing.getDouble("categoryGap"));
        }
    }

    public static StackedBarChart create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Axis<?> xAxis = XYChartActions.getXAxis(self, actionContext);
        Axis<?> yAxis = XYChartActions.getYAxis(self, actionContext);
        StackedBarChart node = new StackedBarChart(xAxis, yAxis);
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }
}
