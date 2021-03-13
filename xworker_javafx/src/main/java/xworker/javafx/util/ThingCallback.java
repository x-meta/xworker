package xworker.javafx.util;

import javafx.util.Callback;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingCallback<P, R> implements Callback<P, R> {
    Thing thing;
    ActionContext actionContext;

    public ThingCallback(Thing thing, ActionContext actionContext){
        this.thing = thing;
        this.actionContext = actionContext;
    }

    @Override
    public R call(P param) {
        return thing.doAction("call", actionContext, "param", param);
    }

    public static ThingCallback create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        ThingCallback<Object, Object> callback = new ThingCallback<Object, Object>(self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), callback);

        return callback;
    }
}
