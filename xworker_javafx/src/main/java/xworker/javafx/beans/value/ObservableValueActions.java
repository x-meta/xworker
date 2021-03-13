package xworker.javafx.beans.value;

import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ObservableValueActions {
    public static void createBindToProperty(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        ObservableValue parent = actionContext.getObject("parent");
        if(parent == null){
            return;
        }

        Object[] properties = self.doAction("getProperties", actionContext);
        if(properties != null){
            for(Object p : properties){
                if(p instanceof Property){
                    Property property = (Property) p;
                    property.bind(parent);
                }
            }
        }
    }
}
