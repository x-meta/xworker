package xworker.javafx.scene.chart;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.Axis;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class BarChartActions {
    public static void init(BarChart node, Thing thing, ActionContext actionContext){
        XYChartActions.init(node, thing, actionContext);

        if(thing.valueExists("barGap")){
            node.setBarGap(thing.getDouble("barGap"));
        }
        if(thing.valueExists("categoryGap")){
            node.setCategoryGap(thing.getDouble("categoryGap"));
        }
    }

    public static BarChart create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Axis<?> xAxis = XYChartActions.getXAxis(self, actionContext);
        Axis<?> yAxis = XYChartActions.getYAxis(self, actionContext);
        BarChart node = new BarChart(xAxis, yAxis);
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }
}
