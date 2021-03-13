package xworker.javafx.beans.property;

import javafx.beans.property.Property;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.value.ObservableSetValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import java.util.HashSet;
import java.util.Set;

public class SimpleSetPropertyActions {
    public static SimpleSetProperty create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Object bean = self.doAction("getBean", actionContext);
        String name = self.getMetadata().getName();
        Object value = self.doAction("getInitialValue", actionContext);
        ObservableSet<Object> initialValue = null;
        if(value instanceof  ObservableSet){
            initialValue = (ObservableSet<Object>) value;
        }else if(value instanceof Set){
            initialValue = FXCollections.observableSet((Set) value);
        }else {
            initialValue = FXCollections.observableSet(new HashSet<>());
        }

        SimpleSetProperty<Object> property = new SimpleSetProperty(bean, name, initialValue);
        actionContext.g().put(name, property);

        actionContext.peek().put("parent", property);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return property;
    }

    public static void createBind(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        SetProperty<Object> parent = actionContext.getObject("parent");

        ObservableSetValue<Object> value = self.doAction("getValue", actionContext);
        if(value != null) {
            parent.bind(value);
        }
    }

    public static void createDindBidirectional(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        SetProperty<Object> parent = actionContext.getObject("parent");

        Property<ObservableSet<Object>> other = self.doAction("getProperty", actionContext);
        if(other != null) {
            parent.bindBidirectional(other);
        }
    }
}
