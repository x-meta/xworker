package xworker.javafx.beans.property;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.value.ObservableValue;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class SimpleFloatPropertyActions {
    public static SimpleFloatProperty create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Object bean = self.doAction("getBean", actionContext);
        String name = self.getMetadata().getName();
        float initialValue = self.doAction("getInitialValue", actionContext);

        SimpleFloatProperty property = new SimpleFloatProperty(bean, name, initialValue);
        actionContext.g().put(name, property);

        actionContext.peek().put("parent", property);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return property;
    }

    public static void createBind(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        FloatProperty parent = actionContext.getObject("parent");

        ObservableValue<Number> value = self.doAction("getValue", actionContext);
        if(value != null) {
            parent.bind(value);
        }
    }

    public static void createDindBidirectional(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        FloatProperty parent = actionContext.getObject("parent");

        Property<Number> other = self.doAction("getProperty", actionContext);
        if(other != null) {
            parent.bindBidirectional(other);
        }
    }
}
