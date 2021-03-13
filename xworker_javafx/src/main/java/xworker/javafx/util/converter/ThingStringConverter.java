package xworker.javafx.util.converter;

import javafx.util.StringConverter;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingStringConverter extends StringConverter<Object> {
    Thing thing;
    ActionContext actionContext;

    public ThingStringConverter(Thing thing, ActionContext actionContext){
        this.thing = thing;
        this.actionContext = actionContext;
    }

    @Override
    public String toString(Object object) {
        return thing.doAction("toString", actionContext, "object", object);
    }

    @Override
    public Object fromString(String string) {
        return thing.doAction("fromString", actionContext, "string", string);
    }

    public static ThingStringConverter create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        ThingStringConverter converter = new ThingStringConverter(self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), converter);

        return converter;
    }

    public static String defaultToString(ActionContext actionContext){
        return String.valueOf(actionContext.get("object"));
    }

    public static Object defaultFromString(ActionContext actionContext){
        return actionContext.get("string");
    }
}
