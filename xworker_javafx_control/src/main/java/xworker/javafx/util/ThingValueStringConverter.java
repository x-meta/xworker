package xworker.javafx.util;

import javafx.util.StringConverter;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import java.util.List;

public class ThingValueStringConverter extends StringConverter<org.xmeta.Thing> {
    static Thing nullValue = new Thing();
    static{
        nullValue.set("label", "");
        nullValue.set("value", "");

    }
    List<Thing> values;

    public ThingValueStringConverter(){
    }

    public ThingValueStringConverter(List<Thing> values){
        this.values = values;
    }

    @Override
    public String toString(org.xmeta.Thing object) {
        if(object == null || object == nullValue){
            return "";
        }else {
            return object.getMetadata().getLabel();
        }
    }

    @Override
    public org.xmeta.Thing fromString(String string) {
        if(string == null){
            return nullValue;
        }

        if(values != null) {
            for (Thing thing : values) {
                if (thing.getMetadata().getLabel().equals(string)) {
                    return thing;
                }
                if(string.equals(thing.getString("value"))){
                    return thing;
                }
            }
        }
        Thing value = new Thing();
        value.set("label", string);
        value.set("value", string);
        return value;
    }

    public static ThingValueStringConverter create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        ThingValueStringConverter converter = new ThingValueStringConverter();
        actionContext.g().put(self.getMetadata().getName(), converter);

        actionContext.peek().put("parent", converter);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return converter;
    }
}
