package xworker.javafx.util;

import javafx.util.StringConverter;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingStringConverter extends StringConverter<Thing> {
    @Override
    public String toString(Thing object) {
        if(object == null){
            return "";
        }
        return object.getMetadata().getLabel();
    }

    @Override
    public Thing fromString(String string) {
        return null;
    }

    public static ThingStringConverter create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        ThingStringConverter converter = new ThingStringConverter();
        actionContext.g().put(self.getMetadata().getName(), converter);

        actionContext.peek().put("parent", converter);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
        return converter;
    }
}
