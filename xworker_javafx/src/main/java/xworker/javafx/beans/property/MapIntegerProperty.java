package xworker.javafx.beans.property;

import javafx.beans.property.SimpleIntegerProperty;
import xworker.util.UtilData;

import java.util.Map;

public class MapIntegerProperty extends SimpleIntegerProperty {
    Map<String, Object> map;
    String name;

    public MapIntegerProperty(Map<String, Object> map, String name){
        this.map = map;
        this.name = name;
    }

    @Override
    public int get() {
        return UtilData.getInt(map.get(name), 0);
    }

    @Override
    public void set(int newValue) {
        super.set(newValue);

        map.put(name, newValue);
    }
}
