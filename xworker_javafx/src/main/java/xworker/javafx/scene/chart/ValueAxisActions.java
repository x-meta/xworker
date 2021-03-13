package xworker.javafx.scene.chart;

import javafx.scene.chart.ValueAxis;
import javafx.util.StringConverter;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;

public class ValueAxisActions {
    public static void init(ValueAxis node, Thing thing, ActionContext actionContext){
        AxisActions.init(node, thing, actionContext);

        if(thing.valueExists("lowerBound")){
            node.setLowerBound(thing.getDouble("lowerBound"));
        }
        if(thing.valueExists("minorTickCount")){
            node.setMinorTickCount(thing.getInt("minorTickCount"));
        }
        if(thing.valueExists("minorTickLength")){
            node.setMinorTickLength(thing.getDouble("minorTickLength"));
        }
        if(thing.valueExists("minorTickVisible")){
            node.setMinorTickVisible(thing.getBoolean("minorTickVisible"));
        }
        if(thing.valueExists("tickLabelFormatter")){
            StringConverter<?> tickLabelFormatter = JavaFXUtils.getObject(thing, "tickLabelFormatter", actionContext);
            if(tickLabelFormatter != null) {
                node.setTickLabelFormatter(tickLabelFormatter);
            }
        }
        if(thing.valueExists("upperBound")){
            node.setUpperBound(thing.getDouble("upperBound"));
        }
    }

    public static void createTickLabelFormatter(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        ValueAxis parent = actionContext.getObject("parent");

        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof StringConverter){
                parent.setTickLabelFormatter((StringConverter) obj);
            }
        }
    }
}
