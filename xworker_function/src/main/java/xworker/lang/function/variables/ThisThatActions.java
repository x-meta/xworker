package xworker.lang.function.variables;

import org.xmeta.ActionContext;

import xworker.ui.function.FunctionRequest;

public class ThisThatActions {
	public static Object getThisLocal(ActionContext actionContext){
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		if(request != null){
			return request.getLocalVariable("This");
		}else{
			return actionContext.get("This");
		}
	}
	
	public static Object getThatLocal(ActionContext actionContext){
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		if(request != null){
			return request.getLocalVariable("That");
		}else{
			return actionContext.get("That");
		}
	}
	
	public static Object getTheseLocal(ActionContext actionContext){
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		if(request != null){
			return request.getLocalVariable("These");
		}else{
			return actionContext.get("These");
		}
	}
	
	public static Object getThoseLocal(ActionContext actionContext){
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		if(request != null){
			return request.getLocalVariable("Those");
		}else{
			return actionContext.get("Those");
		}
	}
}
