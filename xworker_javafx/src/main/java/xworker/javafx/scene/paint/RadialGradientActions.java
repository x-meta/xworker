package xworker.javafx.scene.paint;

import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import java.util.ArrayList;
import java.util.List;

public class RadialGradientActions {
    public static RadialGradient create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        RadialGradient lg = null;
        String valueOf = self.getStringBlankAsNull("valueOf");
        if(valueOf != null){
            lg = RadialGradient.valueOf(valueOf);
        }else{
            List<Stop> stops = new ArrayList<>();
            for(Thing child : self.getChilds()){
                Object obj = child.doAction("create", actionContext);
                if(obj instanceof Stop){
                    stops.add((Stop) obj);
                }
            }

            lg = new RadialGradient(self.getDouble("focusAngle"), self.getDouble("focusDistance"),
                    self.getDouble("centerX"), self.getDouble("centerY"),self.getDouble("radius"),
                    self.getBoolean("proportional"),
                    CycleMethod.valueOf(self.getString("cycleMethod")), stops);
        }
        actionContext.g().put(self.getMetadata().getName(), lg);
        return lg;
    }
}
