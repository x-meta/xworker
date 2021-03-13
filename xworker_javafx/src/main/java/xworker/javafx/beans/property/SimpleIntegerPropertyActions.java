package xworker.javafx.beans.property;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class SimpleIntegerPropertyActions {
    public static SimpleIntegerProperty create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Object bean = self.doAction("getBean", actionContext);
        String name = self.getMetadata().getName();
        int initialValue = self.doAction("getInitialValue", actionContext);

        SimpleIntegerProperty property = new SimpleIntegerProperty(bean, name, initialValue);
        actionContext.g().put(name, property);

        actionContext.peek().put("parent", property);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return property;
    }

    public static void createBind(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        IntegerProperty parent = actionContext.getObject("parent");

        ObservableValue<Number> value = self.doAction("getValue", actionContext);
        if(value != null) {
            parent.bind(value);
        }
    }

    public static void createDindBidirectional(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        IntegerProperty parent = actionContext.getObject("parent");

        Property<Number> other = self.doAction("getProperty", actionContext);
        if(other != null) {
            parent.bindBidirectional(other);
        }
    }
}
