package xworker.javafx.scene.chart;

import javafx.scene.chart.Axis;
import javafx.scene.chart.ScatterChart;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ScatterChartActions {
    public static void init(ScatterChart node, Thing thing, ActionContext actionContext){
        XYChartActions.init(node, thing, actionContext);
    }

    public static ScatterChart create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Axis<?> xAxis = XYChartActions.getXAxis(self, actionContext);
        Axis<?> yAxis = XYChartActions.getYAxis(self, actionContext);
        ScatterChart node = new ScatterChart(xAxis, yAxis);
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }
}
