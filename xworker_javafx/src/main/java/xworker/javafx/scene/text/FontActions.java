package xworker.javafx.scene.text;

import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import java.util.ArrayList;
import java.util.List;

public class FontActions {
    public static List<Thing> getFamilies(){
        List<Thing> list = new ArrayList<>();

        for(String name : Font.getFamilies()){
            Thing thing = new Thing("xworker.lang.MetaDescriptor3/@attribute/@value");
            thing.set("name", name);
            thing.set("value", name);
            list.add(thing);
        }

        return list;
    }

    public static Font create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        String family= self.getStringBlankAsNull("family");

        FontWeight weight = null;
        if(self.valueExists("weight")) {
            weight = FontWeight.valueOf(self.getString("weight"));
        }
        FontPosture posture = null;
        if(self.valueExists("posture")){
            posture = FontPosture.valueOf(self.getString("posture"));
        }
        double size = self.getDouble("size");

        if(weight != null && posture != null){
            return Font.font(family, weight, posture, size);
        }else if(weight != null){
            return Font.font(family, weight, size);
        }else if(size > 0){
            return Font.font(family, size);
        }else {
            return Font.font(family);
        }


    }
}
