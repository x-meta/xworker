package xworker.javafx.util;

import javafx.util.StringConverter;
import xworker.util.ThingGroup;

public class ThingGroupStringConverter extends StringConverter<ThingGroup> {
    @Override
    public String toString(ThingGroup object) {
        if(object == null){
            return "";
        }
        if(object.getThing() == null){
            return object.getGroup();
        }else{
            return object.getThing().getMetadata().getLabel();
        }
    }

    @Override
    public ThingGroup fromString(String string) {
        return null;
    }
}
