package xworker.javafx.beans.property;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class SimpleObjectPropertyActions {
    public static SimpleObjectProperty create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Object bean = self.doAction("getBean", actionContext);
        String name = self.getMetadata().getName();
        Object initialValue = self.doAction("getInitialValue", actionContext);

        SimpleObjectProperty<Object> property = new SimpleObjectProperty(bean, name, initialValue);
        actionContext.g().put(name, property);

        actionContext.peek().put("parent", property);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return property;
    }

    public static void createBind(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        ObjectProperty<Object> parent = actionContext.getObject("parent");

        ObservableObjectValue<Object> value = self.doAction("getValue", actionContext);
        if(value != null) {
            parent.bind(value);
        }
    }

    public static void createDindBidirectional(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        ObjectProperty<Object> parent = actionContext.getObject("parent");

        Property<Object> other = self.doAction("getProperty", actionContext);
        if(other != null) {
            parent.bindBidirectional(other);
        }
    }
}
