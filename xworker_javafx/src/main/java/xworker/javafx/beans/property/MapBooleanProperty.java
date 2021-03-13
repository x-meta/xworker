package xworker.javafx.beans.property;

import javafx.beans.property.SimpleBooleanProperty;
import xworker.util.UtilData;

import java.util.Map;

public class MapBooleanProperty extends SimpleBooleanProperty {
    Map<String, Object> map;
    String name;

    public MapBooleanProperty(Map<String, Object> map, String name){
        this.map = map;
        this.name = name;
    }

    @Override
    public boolean get() {
        return UtilData.getBoolean(map.get(name), false);
    }

    @Override
    public void set(boolean newValue) {
        super.set(newValue);

        map.put(name, newValue);
    }
}
