package xworker.javafx.util;

import javafx.beans.property.SimpleMapProperty;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.lang.actions.ActionContainer;

public class RunAction {
    public static Object create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        Object result = null;
        for(Thing child : self.getChilds()){
            result = child.getAction().run(actionContext);
        }

        return result;
    }

    public static Object createActionContainer(ActionContext actionContext){
        Thing self = (Thing) actionContext.get("self");

        ActionContainer scriptContainer = new ActionContainer(self, actionContext);
        for(Thing ext : self.getExtends()) {
            scriptContainer.append(ext);
        }

        actionContext.getScope(0).put(self.getString("name"), scriptContainer);
        return scriptContainer;
    }
}
