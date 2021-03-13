package xworker.javafx.scene.chart;

import javafx.collections.ObservableList;
import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;

public class XYChartActions {
    public static void init(XYChart node, Thing thing, ActionContext actionContext){
        ChartActions.init(node, thing, actionContext);

        if(thing.valueExists("alternativeColumnFillVisible")){
            node.setAlternativeColumnFillVisible(thing.getBoolean("alternativeColumnFillVisible"));
        }
        if(thing.valueExists("alternativeRowFillVisible")){
            node.setAlternativeRowFillVisible(thing.getBoolean("alternativeRowFillVisible"));
        }
        if(thing.valueExists("data")){
            ObservableList<XYChart.Series<?,?>> data = JavaFXUtils.getObject(thing, "data", actionContext);
            if(data != null) {
                node.setData(data);
            }
        }
        if(thing.valueExists("horizontalGridLinesVisible")){
            node.setHorizontalGridLinesVisible(thing.getBoolean("horizontalGridLinesVisible"));
        }
        if(thing.valueExists("horizontalZeroLineVisible")){
            node.setHorizontalZeroLineVisible(thing.getBoolean("horizontalZeroLineVisible"));
        }
        if(thing.valueExists("verticalGridLinesVisible")){
            node.setVerticalGridLinesVisible(thing.getBoolean("verticalGridLinesVisible"));
        }
        if(thing.valueExists("verticalZeroLineVisible")){
            node.setVerticalZeroLineVisible(thing.getBoolean("verticalZeroLineVisible"));
        }
    }

    public static Axis<?> getXAxis(Thing thing, ActionContext actionContext){
        Axis<?> axis = JavaFXUtils.getObject(thing, "xAxis", actionContext);
        if(axis != null){
            return axis;
        }

        Thing axisThing = thing.getThing("XAxis@0");
        if(axisThing != null) {
            for (Thing child : axisThing.getChilds()) {
                Object obj = child.doAction("create", actionContext);
                if(obj instanceof  Axis){
                    return (Axis<?>) obj;
                }
            }
        }

        return null;
    }

    public static Axis<?> getYAxis(Thing thing, ActionContext actionContext){
        Axis<?> axis = JavaFXUtils.getObject(thing, "yAxis", actionContext);
        if(axis != null){
            return axis;
        }

        Thing axisThing = thing.getThing("YAxis@0");
        if(axisThing != null) {
            for (Thing child : axisThing.getChilds()) {
                Object obj = child.doAction("create", actionContext);
                if(obj instanceof  Axis){
                    return (Axis<?>) obj;
                }
            }
        }

        return null;
    }
}
