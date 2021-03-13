package xworker.javafx.scene.chart;

import javafx.geometry.Side;
import javafx.scene.chart.Axis;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.layout.RegionActions;
import xworker.javafx.scene.text.FontActions;
import xworker.javafx.util.JavaFXUtils;


public class AxisActions {
    public static void init(Axis<?> node, Thing thing, ActionContext actionContext){
        RegionActions.init(node, thing, actionContext);

        if(thing.valueExists("animated")){
            node.setAnimated(thing.getBoolean("animated"));
        }
        if(thing.valueExists("autoRanging")){
            node.setAutoRanging(thing.getBoolean("autoRanging"));
        }
        if(thing.valueExists("label")){
            String label = JavaFXUtils.getString(thing, "label", actionContext);
            if(label != null) {
                node.setLabel(label);
            }
        }
        if(thing.valueExists("side")){
            node.setSide(Side.valueOf(thing.getString("side")));
        }
        if(thing.valueExists("tickLabelFill")){
            Paint tickLabelFill = JavaFXUtils.getObject(thing, "tickLabelFill", actionContext);
            if(tickLabelFill != null) {
                node.setTickLabelFill(tickLabelFill);
            }
        }
        if(thing.valueExists("tickLabelFont")){
            Font tickLabelFont = JavaFXUtils.getFont(thing.getString("tickLabelFont"), actionContext);
            if(tickLabelFont != null) {
                node.setTickLabelFont(tickLabelFont);
            }
        }
        if(thing.valueExists("tickLabelGap")){
            node.setTickLabelGap(thing.getDouble("tickLabelGap"));
        }
        if(thing.valueExists("tickLabelRotation")){
            node.setTickLabelRotation(thing.getDouble("tickLabelRotation"));
        }
        if(thing.valueExists("tickLabelsVisible")){
            node.setTickLabelsVisible(thing.getBoolean("tickLabelsVisible"));
        }
        if(thing.valueExists("tickLength")){
            node.setTickLength(thing.getDouble("tickLength"));
        }
        if(thing.valueExists("tickMarkVisible")){
            node.setTickMarkVisible(thing.getBoolean("tickMarkVisible"));
        }
    }

    public static void createTickLabelFill(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Axis<?> parent = actionContext.getObject("parent");

        for(Thing child : self.getChilds()){
            Object obj = child.doAction("create", actionContext);
            if(obj instanceof Paint){
                parent.setTickLabelFill((Paint) obj);
            }
        }
    }

    public static void createTickLabelFont(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Axis<?> parent = actionContext.getObject("parent");
        Font font = FontActions.create(actionContext);
        parent.setTickLabelFont(font);
    }
}
