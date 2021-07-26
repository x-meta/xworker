package xworker.lang.executor.requests;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;
import xworker.lang.executor.Executor;
import xworker.lang.executor.Request;
import xworker.util.UtilData;

import java.util.Map;

public class ThingForm {
    private static final String TAG = ThingForm.class.getName();

    public static Object create(ActionContext actionContext){
        Thing self = actionContext.getObject("self");

        String platform = actionContext.getObject("platform");

        Boolean validate = self.doAction("isValidate", actionContext);
        String thingType = self.doAction("getThingType", actionContext);
        Thing thing = self.doAction("getThing", actionContext);

        actionContext.g().put("validate", validate);
        actionContext.g().put("thingType", thingType);
        actionContext.g().put("thing", thing);

        if("swt".equals(platform)){
            Thing prototype = World.getInstance().getThing("xworker.lang.executor.requests.prototypes.ThingForm/@ThingLoader");
            return prototype.doAction("create", actionContext);
        }

        Executor.warn(TAG, "ThingFomr request not supported by platform " + platform);
        return null;
    }

    public static Object ok(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        ActionContainer actionContainer = actionContext.getObject("actions");
        if(actionContainer != null){
            if(UtilData.isTrue(actionContext.get("validate"))){
                if(UtilData.isTrue(actionContainer.doAction("validate", actionContext))){
                    Map<String, Object> values = actionContainer.doAction("getValues", actionContext);
                    return self.doAction("onOk", actionContext, "values", values);
                }else{
                    return self.doAction("onValidateFalse", actionContext);
                }
            }else{
                Map<String, Object> values = actionContainer.doAction("getValues", actionContext);
                return self.doAction("onOk", actionContext, "values", values);
            }
        }

        return null;
    }

    public static void cancel(ActionContext actionContext){
        Thing self = actionContext.getObject("self");
        self.doAction("onCancel", actionContext);

        Request request = actionContext.getObject("request");
        request.finish();
    }
}
