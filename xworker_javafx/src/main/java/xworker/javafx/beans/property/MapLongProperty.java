package xworker.javafx.beans.property;

import javafx.beans.property.SimpleLongProperty;
import xworker.util.UtilData;

import java.util.Map;

public class MapLongProperty extends SimpleLongProperty {
    Map<String, Object> map;
    String name;

    public MapLongProperty(Map<String, Object> map, String name){
        this.map = map;
        this.name = name;
    }

    @Override
    public long get() {
        return UtilData.getLong(map.get(name), 0);
    }

    @Override
    public void set(long newValue) {
        super.set(newValue);

        map.put(name, newValue);
    }
}
