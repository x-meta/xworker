package xworker.javafx.util;

import javafx.fxml.FXML;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.ThingLoader;

public class FXThingLoader {
    public static void push(Object object){
        ThingLoader.push(object);
    }

    public static void pop(){
        ThingLoader.pop();
    }
    /**
     * 执行thing的create(actionContext)方法，执行时如果是EventHandler等可能会吧object的相应方法绑定到handler上，
     * 最后会把对象的@FXML和@ActionField等注解的字段赋值。
     */
    public static <T> T load(Object object, Thing thing, ActionContext actionContext){
        final T load = (T) ThingLoader.load(object, thing, actionContext, FXML.class);
        return load;
    }

    public static Object getObject(){
        return ThingLoader.getObject();
    }
}
