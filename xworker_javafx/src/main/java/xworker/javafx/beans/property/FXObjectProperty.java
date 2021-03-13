package xworker.javafx.beans.property;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class FXObjectProperty {
    public static void create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Object property = self.doAction("getProperty", actionContext);
        actionContext.g().put(self.getMetadata().getName(), property);

        actionContext.peek().put("parent", property);
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }
    }
}
