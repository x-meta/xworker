package xworker.lang.executor.requests;

import java.util.Collections;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;

import xworker.lang.executor.ExecuteRequestSwtUtils;

//对应的模型是：xworker.lang.executor.requests.RequestForm
public class RequestForm {
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		//请求的变量上下文
		ActionContext requestContext = actionContext.getObject("requestContext");
		if(requestContext == null) {
			requestContext = actionContext;
		}
		
		Thing formThing = self.doAction("getFormThing", requestContext);
		Map<String, Object> values = self.doAction("getDefaultValues", requestContext);
		if(values == null) {
			values = Collections.emptyMap();
		}
		Thing prototypes = World.getInstance().getThing("xworker.lang.executor.requests.prototypes.RequestPrototypes/@formComposite");
		Object obj = ExecuteRequestSwtUtils.createOkCancelControl(prototypes, actionContext);
		
		//Object composite = prototypes.doAction("create", actionContext);
		ActionContainer actions = actionContext.getObject("actions");
		actions.doAction("doInit", actionContext, "formThing", formThing, "values", values);
		
		return obj;
	}
	
	public static void init(ActionContext actionContext) {
		Thing thingForm = actionContext.getObject("thingForm");
		thingForm.doAction("setDescriptor", actionContext, "descriptor", actionContext.getObject("formThing"));
		thingForm.doAction("setValues", actionContext);
	}
	
	public static Object getResult(ActionContext actionContext) {
		Thing thingForm = actionContext.getObject("thingForm");
		return thingForm.doAction("getValues", actionContext);
	}
	
	public static Object validate(ActionContext actionContext) {
		Thing thingForm = actionContext.getObject("thingForm");
		return thingForm.doAction("validate", actionContext);
	}
}
