package xworker.javafx.beans.property;

import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableListValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleListPropertyActions {
    public static SimpleListProperty create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Object bean = self.doAction("getBean", actionContext);
        String name = self.getMetadata().getName();
        Object value = self.doAction("getInitialValue", actionContext);;
        ObservableList<Object> initialValue = null;
        if(value instanceof ObservableList){
            initialValue = (ObservableList<Object>) value;
        }else if(initialValue instanceof List){
            initialValue = FXCollections.observableList((List) value);
        }else {
            initialValue = FXCollections.observableList(new ArrayList<>());
        }

        SimpleListProperty<Object> property = new SimpleListProperty(bean, name, initialValue);
        actionContext.g().put(name, property);

        actionContext.peek().put("parent", property);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return property;
    }

    public static void createBind(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        ListProperty<Object> parent = actionContext.getObject("parent");

        ObservableListValue<Object> value = self.doAction("getValue", actionContext);
        if(value != null) {
            parent.bind(value);
        }
    }

    public static void createDindBidirectional(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        ListProperty<Object> parent = actionContext.getObject("parent");

        Property<ObservableList<Object>> other = self.doAction("getProperty", actionContext);
        if(other != null) {
            parent.bindBidirectional(other);
        }
    }
}
