package xworker.javafx.util;

import javafx.util.Callback;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingCallback<P, R> implements Callback<P, R> {
    Thing thing;
    ActionContext actionContext;
    Object object;

    public ThingCallback(Thing thing, ActionContext actionContext){
        this.thing = thing;
        this.actionContext = actionContext;
        object = FXThingLoader.getObject();
    }

    @Override
    public R call(P param) {
        if(object != null){
            FXThingLoader.push(object);
            try{
                return thing.doAction("call", actionContext, "param", param);
            }finally {
                FXThingLoader.pop();
            }
        }else{
            return thing.doAction("call", actionContext, "param", param);
        }

    }

    public static ThingCallback create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        ThingCallback<Object, Object> callback = new ThingCallback<Object, Object>(self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), callback);

        return callback;
    }
}
