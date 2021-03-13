package xworker.javafx.scene.chart;

import javafx.geometry.Side;
import javafx.scene.chart.Chart;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.layout.RegionActions;
import xworker.javafx.util.JavaFXUtils;

public class ChartActions {
    public static void init(Chart node, Thing thing, ActionContext actionContext){
        RegionActions.init(node, thing, actionContext);

        if(thing.valueExists("animated")){
            node.setAnimated(thing.getBoolean("animated"));
        }
        if(thing.valueExists("legendSide")){
            node.setLegendSide(Side.valueOf(thing.getString("legendSide")));
        }
        if(thing.valueExists("legendVisible")){
            node.setLegendVisible(thing.getBoolean("legendVisible"));
        }
        if(thing.valueExists("title")){
            String title = JavaFXUtils.getString(thing, "title", actionContext);
            if(title != null) {
                node.setTitle(title);
            }
        }
        if(thing.valueExists("titleSide")){
            node.setTitleSide(Side.valueOf(thing.getString("titleSide")));
        }

    }
}
