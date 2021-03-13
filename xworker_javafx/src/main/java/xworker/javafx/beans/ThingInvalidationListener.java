package xworker.javafx.beans;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingInvalidationListener implements InvalidationListener {
    Thing thing;
    ActionContext actionContext;

    public ThingInvalidationListener(Thing thing, ActionContext actionContext){
        this.thing = thing;
        this.actionContext = actionContext;
    }

    @Override
    public void invalidated(Observable observable) {
        thing.doAction("invalidated", actionContext, "observable", observable);
    }

    public static ThingInvalidationListener create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        ThingInvalidationListener listener = new ThingInvalidationListener(self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), listener);

        Object parent = actionContext.getObject("parent");
        if(parent instanceof Observable){
            ((Observable) parent).addListener(listener);
        }
        return listener;
    }
}
