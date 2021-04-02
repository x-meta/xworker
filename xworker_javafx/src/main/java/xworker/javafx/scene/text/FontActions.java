package xworker.javafx.scene.text;

import javafx.scene.control.Labeled;
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
        if(size == 0){
            size = -1;
        }

        Font font = null;
        if(weight != null && posture != null){
            font = Font.font(family, weight, posture, size);
        }else if(weight != null){
            font = Font.font(family, weight, size);
        }else if(size > 0){
            font = Font.font(family, size);
        }else {
            font = Font.font(family);
        }

        actionContext.g().put(self.getMetadata().getName(), font);
        Object parent = actionContext.getObject("parent");
        if(parent instanceof Labeled){
            ((Labeled) parent).setFont(font);
        }

        return font;

    }
}
