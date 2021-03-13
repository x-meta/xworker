package xworker.javafx.scene.chart;

import javafx.scene.chart.Axis;
import javafx.scene.chart.BubbleChart;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class BubbleChartActions {
    public static void init(BubbleChart node, Thing thing, ActionContext actionContext){
        XYChartActions.init(node, thing, actionContext);
    }

    public static BubbleChart create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Axis<?> xAxis = XYChartActions.getXAxis(self, actionContext);
        Axis<?> yAxis = XYChartActions.getYAxis(self, actionContext);
        BubbleChart node = new BubbleChart(xAxis, yAxis);
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }
}
