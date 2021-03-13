package xworker.javafx.beans.value;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import java.lang.reflect.Method;

public class ThingChangeListener implements ChangeListener {
    Thing thing;
    ActionContext actionContext;

    public ThingChangeListener(Thing thing, ActionContext actionContext){
        this.thing = thing;
        this.actionContext = actionContext;
    }

    @Override
    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
        thing.doAction("changed", actionContext, "observable",observable,
                "oldValue", oldValue, "newValue", newValue);
    }

    public static ThingChangeListener create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        ThingChangeListener listener = new ThingChangeListener(self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), listener);

        Object parent = actionContext.getObject("parent");
        if(parent != null){
            try{
                Method method = parent.getClass().getMethod("addListener", ChangeListener.class);
                if(method != null){
                    method.invoke(parent, listener);
                }
            }catch(Exception e){
            }
        }
        return listener;
    }
}
