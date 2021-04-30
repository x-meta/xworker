package xworker.javafx.util;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import java.util.List;

public class DataSelector implements Selector<Object>{
    Thing thing;
    ActionContext actionContext;

    public DataSelector(Thing thing, ActionContext actionContext){
        this.thing = thing;
        this.actionContext = actionContext;
    }

    @Override
    public List<Object> getSelectItems() {
        return thing.doAction("getSelectItems", actionContext);
    }

    @Override
    public Object getSelectItem() {
        return thing.doAction("getSelectItem", actionContext);
    }

    public static DataSelector create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        return new DataSelector(self, actionContext);
    }
}
