package xworker.javafx.beans.property;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import java.util.*;

public class MapAdapter<K, V> implements Map<K, V> {
    private ObservableMap<K, Property> valueMap = FXCollections.observableMap(new HashMap<>());
    Thing thing;
    ActionContext actionContext;

    public MapAdapter(Thing thing, ActionContext actionContext){
        this.thing = thing;
        this.actionContext = actionContext;
    }

    public MapAdapter(){

    }

    public ObservableMap<K, Property> getValueMap(){
        return valueMap;
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

    public void setProperty(K key, Property<Object> property){
        valueMap.put(key, property);
    }

    public Property<Object> getProperty(K key){
        return valueMap.get(key);
    }

    @Override
    public V get(Object key) {
        Property<?> p = valueMap.get(key);
        if(p != null){
            return (V) p.getValue();
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
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
    public V remove(Object key) {
        return (V) valueMap.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for(K key : m.keySet()){
            put(key, m.get(key));
        }
    }

    @Override
    public void clear() {
        valueMap.clear();
    }

    @Override
    public Set<K> keySet() {
        return valueMap.keySet();
    }

    @Override
    public Collection<V> values() {
        List<V> values = new ArrayList<>();

        for(Property  p : valueMap.values()){
            values.add((V) p.getValue());
        }

        return values;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }
}
