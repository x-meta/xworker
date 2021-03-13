package xworker.javafx.beans.property;

import javafx.beans.property.SimpleFloatProperty;
import xworker.util.UtilData;

import java.util.Map;

public class MapFloatProperty extends SimpleFloatProperty {
    Map<String, Object> map;
    String name;

    public MapFloatProperty(Map<String, Object> map, String name){
        this.map = map;
        this.name = name;
    }

    @Override
    public float get() {
        return UtilData.getFloat(map.get(name), 0);
    }

    @Override
    public void set(float newValue) {
        super.set(newValue);

        map.put(name, newValue);
    }
}
