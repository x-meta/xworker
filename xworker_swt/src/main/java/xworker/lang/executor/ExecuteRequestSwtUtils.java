package xworker.lang.executor;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;
import org.xmeta.util.UtilData;

public class ExecuteRequestSwtUtils {
	public static void okButtonSelection(ActionContext actionContext) {
		Request request = actionContext.getObject("request");
		Executor.push(request.getExecutorService());
		try {
			ActionContainer actions = actionContext.getObject("actions");
			
			Object r = null;
			if(actions != null) {
				Object validate = actions.doAction("validate", actionContext);
				if(validate != null && UtilData.isTrue(validate) == false) {
					//校验失败
					return;
				}
				
				//异步执行结果
				r = actions.doAction("getResult", actionContext);	
			}
			
			request.doAction("ok", "result", r);
			request.finish();
		} finally {
			Executor.pop();
		}
	}
	
	public static void cancelButtonSelection(ActionContext actionContext) {
		Request request = actionContext.getObject("request");
		Executor.push(request.getExecutorService());
		try {
			request.doAction("cancel");
			request.finish();
		} finally {
			Executor.pop();
		}
	}
	
	public static Object createOkCancelControl(Thing thing, ActionContext actionContext) {
		//创建okcacnel的原型
		Thing okcancelProto = World.getInstance().getThing("xworker.lang.executor.requests.prototypes.OkCancelRequest/@Composite");
		Object result = okcancelProto.doAction("create", actionContext);
		
		//requestComposite
		actionContext.peek().put("parent", actionContext.get("requestComposite"));
		thing.doAction("create", actionContext);
		
		return result;
	}
}
