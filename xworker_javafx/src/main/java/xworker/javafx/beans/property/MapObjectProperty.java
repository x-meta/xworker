package xworker.javafx.beans.property;

import javafx.beans.property.SimpleObjectProperty;
import xworker.util.UtilData;

import java.util.Map;

public class MapObjectProperty extends SimpleObjectProperty {
    Map<String, Object> map;
    String name;

    public MapObjectProperty(Map<String, Object> map, String name){
        this.map = map;
        this.name = name;
    }

    @Override
    public Object get() {
        return map.get(name);
    }

    @Override
    public void set(Object newValue) {
        super.set(newValue);

        map.put(name, newValue);
    }
}
