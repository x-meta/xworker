package xworker.javafx.beans.property;

import javafx.beans.property.MapProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.value.ObservableMapValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import java.util.HashMap;
import java.util.Map;

public class SimpleMapPropertyActions {
    public static SimpleMapProperty create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Object bean = self.doAction("getBean", actionContext);
        String name = self.getMetadata().getName();
        Object value = self.doAction("getInitialValue", actionContext);
        ObservableMap<Object, Object> initialValue = null;

        if(value instanceof  ObservableMap){
            initialValue = (ObservableMap<Object, Object>) value;
        }else if(value instanceof Map){
            initialValue = FXCollections.observableMap((Map) value);
        }else{
            initialValue = FXCollections.observableMap(new HashMap<>());
        }

        SimpleMapProperty<Object, Object> property = new SimpleMapProperty(bean, name, initialValue);
        actionContext.g().put(name, property);

        actionContext.peek().put("parent", property);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return property;
    }

    public static void createBind(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        MapProperty<Object, Object> parent = actionContext.getObject("parent");

        ObservableMapValue<Object, Object> value = self.doAction("getValue", actionContext);
        if(value != null) {
            parent.bind(value);
        }
    }

    public static void createDindBidirectional(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        MapProperty<Object, Object> parent = actionContext.getObject("parent");

        Property<ObservableMap<Object, Object>> other = self.doAction("getProperty", actionContext);
        if(other != null) {
            parent.bindBidirectional(other);
        }
    }
}
