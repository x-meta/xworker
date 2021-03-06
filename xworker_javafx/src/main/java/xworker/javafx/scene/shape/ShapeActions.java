package xworker.javafx.scene.shape;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.scene.NodeActions;
import xworker.javafx.util.JavaFXUtils;

public class ShapeActions {
    public static void init(Shape node, Thing thing, ActionContext actionContext){
        NodeActions.init(node, thing, actionContext);

        if(thing.valueExists("fill")){
            Paint fill = JavaFXUtils.getObject(thing, "fill", actionContext);
            if(fill != null) {
                node.setFill(fill);
            }
        }
        if(thing.valueExists("smooth")){
            node.setSmooth(thing.getBoolean("smooth"));
        }
        if(thing.valueExists("strokeDashOffset")){
            node.setStrokeDashOffset(thing.getDouble("strokeDashOffset"));
        }
        if(thing.valueExists("strokeLineCap")){
            node.setStrokeLineCap(StrokeLineCap.valueOf(thing.getString("strokeLineCap")));
        }
        if(thing.valueExists("strokeLineJoin")){
            node.setStrokeLineJoin(StrokeLineJoin.valueOf(thing.getString("strokeLineJoin")));
        }
        if(thing.valueExists("strokeMiterLimit")){
            node.setStrokeMiterLimit(thing.getDouble("strokeMiterLimit"));
        }
        if(thing.valueExists("stroke")){
            Paint stroke = JavaFXUtils.getObject(thing, "stroke", actionContext);
            if(stroke != null) {
                node.setStroke(stroke);
            }
        }
        if(thing.valueExists("strokeType")){
            node.setStrokeType(StrokeType.valueOf(thing.getString("strokeType")));
        }
        if(thing.valueExists("strokeWidth")){
            node.setStrokeWidth(thing.getDouble("strokeWidth"));
        }
    }
}
