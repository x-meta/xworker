package xworker.javafx.beans.property;

import javafx.beans.property.*;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import java.util.*;

public class MapAdapter implements Map<String, Object> {
    private Map<String, Property> valueMap = new HashMap<>();
    Thing thing;
    ActionContext actionContext;

    public MapAdapter(Thing thing, ActionContext actionContext){
        this.thing = thing;
        this.actionContext = actionContext;
    }


    @Override
    public int size() {
        return valueMap.size();
    }

    @Override
    public boolean isEmpty() {
        return valueMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return valueMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return valueMap.containsValue(value);
    }

    public void setProperty(String key, Property property){
        valueMap.put(key, property);
    }

    public Property getProperty(String key){
        return valueMap.get(key);
    }

    @Override
    public Object get(Object key) {
        Property<?> p = valueMap.get(key);
        if(p != null){
            return p.getValue();
        }
        return null;
    }

    @Override
    public Object put(String key, Object value) {
        Property p = valueMap.get(key);
        if(p != null){
            p.setValue(value);
        }else{
            for(Thing child : thing.getChilds()){
                if(child.getMetadata().getName().equals(key)){
                    String thingName = child.getThingName();
                    if("BooleanProperty".equals(thingName)){
                        p = new SimpleBooleanProperty();
                    }else if("DoubleProperty".equals(thingName)){
                        p = new SimpleDoubleProperty();
                    }else if("FloatProperty".equals(thingName)){
                        p = new SimpleFloatProperty();
                    }else if("IntegerProperty".equals(thingName)){
                        p = new SimpleIntegerProperty();
                    }else if("LongProperty".equals(thingName)){
                        p = new SimpleLongProperty();
                    }else{
                        p = new SimpleObjectProperty();
                    }

                    if(p != null){
                        break;
                    }
                }
            }

            if(p == null){
                p = new SimpleObjectProperty();
            }
            valueMap.put(key, p);
            p.setValue(value);
        }

        return value;
    }

    @Override
    public Object remove(Object key) {
        return valueMap.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        for(String key : m.keySet()){
            put(key, m.get(key));
        }
    }

    @Override
    public void clear() {
        valueMap.clear();
    }

    @Override
    public Set<String> keySet() {
        return valueMap.keySet();
    }

    @Override
    public Collection<Object> values() {
        List<Object> values = new ArrayList<>();

        for(Property  p : valueMap.values()){
            values.add(p.getValue());
        }

        return values;
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        throw new UnsupportedOperationException();
    }
}
