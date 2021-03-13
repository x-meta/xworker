package xworker.javafx.beans.property;

import javafx.beans.property.SimpleStringProperty;
import xworker.util.UtilData;

import java.util.Map;

public class MapStringProperty extends SimpleStringProperty{
    Map<String, Object> map;
    String name;

    public MapStringProperty(Map<String, Object> map, String name){
        this.map = map;
        this.name = name;
    }

    @Override
    public String get() {
        return UtilData.getString(map.get(name), null);
    }

    @Override
    public void set(String newValue) {
        super.set(newValue);

        map.put(name, newValue);
    }
}
