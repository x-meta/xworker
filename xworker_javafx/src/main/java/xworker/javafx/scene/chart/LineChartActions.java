package xworker.javafx.scene.chart;

import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class LineChartActions {
    public static void init(LineChart node, Thing thing, ActionContext actionContext){
        XYChartActions.init(node, thing, actionContext);

        if(thing.valueExists("axisSortingPolicy")){
            node.setAxisSortingPolicy(LineChart.SortingPolicy.valueOf(thing.getString("axisSortingPolicy")));
        }
        if(thing.valueExists("createSymbols")){
            node.setCreateSymbols(thing.getBoolean("createSymbols"));
        }
    }

    public static LineChart create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Axis<?> xAxis = XYChartActions.getXAxis(self, actionContext);
        Axis<?> yAxis = XYChartActions.getYAxis(self, actionContext);
        LineChart node = new LineChart(xAxis, yAxis);
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }
}
