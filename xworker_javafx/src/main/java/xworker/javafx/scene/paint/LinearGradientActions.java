package xworker.javafx.scene.paint;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.javafx.util.JavaFXUtils;

import java.util.ArrayList;
import java.util.List;

public class LinearGradientActions {
    public static LinearGradient create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        LinearGradient lg = null;
        String valueOf = self.getStringBlankAsNull("valueOf");
        if(valueOf != null){
            lg = LinearGradient.valueOf(valueOf);
        }else{
            List<Stop> stops = new ArrayList<>();
            for(Thing child : self.getChilds()){
                Object obj = child.doAction("create", actionContext);
                if(obj instanceof Stop){
                    stops.add((Stop) obj);
                }
            }

            lg = new LinearGradient(self.getDouble("startX"), self.getDouble("startY"),
                    self.getDouble("endX"), self.getDouble("endY"), self.getBoolean("proportional"),
                    CycleMethod.valueOf(self.getString("cycleMethod")), stops);
        }
        actionContext.g().put(self.getMetadata().getName(), lg);
        return lg;
    }

    public static Stop createStop(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Color color = JavaFXUtils.getColor(self, "color", actionContext);
        return new Stop(self.getDouble("offset"), color);
    }
}
