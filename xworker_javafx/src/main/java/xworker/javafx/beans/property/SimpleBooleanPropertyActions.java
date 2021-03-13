package xworker.javafx.beans.property;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class SimpleBooleanPropertyActions {
    public static SimpleBooleanProperty create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        Object bean = self.doAction("getBean", actionContext);
        String name = self.getMetadata().getName();
        boolean initialValue = self.doAction("getInitialValue", actionContext);

        SimpleBooleanProperty property = new SimpleBooleanProperty(bean, name, initialValue);
        actionContext.g().put(name, property);

        actionContext.peek().put("parent", property);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return property;
    }

    public static void createBind(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        BooleanProperty parent = actionContext.getObject("parent");

        ObservableValue<Boolean> value = self.doAction("getValue", actionContext);
        if(value != null) {
            parent.bind(value);
        }
    }

    public static void createDindBidirectional(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        BooleanProperty parent = actionContext.getObject("parent");

        Property<Boolean> other = self.doAction("getProperty", actionContext);
        if(other != null) {
            parent.bindBidirectional(other);
        }
    }
}
