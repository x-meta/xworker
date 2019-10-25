package xworker.lang.function;

import org.xmeta.ActionContext;

import xworker.ui.function.FunctionRequest;

public class FunctionUtil {
	public static Object getLocalValue(String name, ActionContext actionContext){
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		if(request == null){
			return actionContext.get(name);
		}else{
			return request.getLocalValue(name);
		}
	}
}
