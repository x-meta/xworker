package xworker.javafx.scene.chart;

import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;

public class PieChartActions {
    public static void init(PieChart node, Thing thing, ActionContext actionContext){
        ChartActions.init(node, thing, actionContext);

        if(thing.valueExists("clockwise")){
            node.setClockwise(thing.getBoolean("clockwise"));
        }
        if(thing.valueExists("data")){
            ObservableList<PieChart.Data> data = JavaFXUtils.getObject(thing, "data", actionContext);
            if(data != null) {
                node.setData(data);
            }
        }
        if(thing.valueExists("labelLineLength")){
            node.setLabelLineLength(thing.getDouble("labelLineLength"));
        }
        if(thing.valueExists("labelsVisible")){
            node.setLabelsVisible(thing.getBoolean("labelsVisible"));
        }
        if(thing.valueExists("startAngle")){
            node.setStartAngle(thing.getDouble("startAngle"));
        }
    }

    public static PieChart create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        PieChart node = new PieChart();
        init(node, self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), node);

        actionContext.peek().put("parent", node);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return node;
    }
}
