package xworker.javafx.thing.form;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ThingFormActions {
    public static Object create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        ThingForm thingForm = new ThingForm(self, actionContext);
        actionContext.g().put(self.getMetadata().getName(), thingForm);

        actionContext.peek().put("parent", thingForm.getFormNode());
        for(Thing child : self.getChilds()){
            child.doAction("create", actionContext);
        }

        return thingForm.getFormNode();
    }
}
