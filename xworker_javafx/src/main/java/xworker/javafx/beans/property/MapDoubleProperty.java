package xworker.javafx.beans.property;

import javafx.beans.property.SimpleDoubleProperty;
import xworker.util.UtilData;

import java.util.Map;

public class MapDoubleProperty extends SimpleDoubleProperty {
    Map<String, Object> map;
    String name;

    public MapDoubleProperty(Map<String, Object> map, String name){
        this.map = map;
        this.name = name;
    }

    @Override
    public double get() {
        return UtilData.getDouble(map.get(name), 0d);
    }

    @Override
    public void set(double newValue) {
        super.set(newValue);

        map.put(name, newValue);
    }
}
