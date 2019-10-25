package xworker.ui.function.functionUtils;

import org.xmeta.ActionContext;

import xworker.ui.function.FunctionRequest;
import xworker.ui.function.FunctionRequestUIFactory;

public class CloseFunctionUI {
	public static void doFunction(ActionContext actionContext){		
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		
		if(request != null){
			FunctionRequestUIFactory.closeUI(request.getRoot());
		}
	}
	
	public static void closeUIOnFinished(ActionContext actionContext){
        FunctionRequest request = (FunctionRequest) actionContext.get("request");
		
		if(request != null){
			request.getRoot().setCloseUIOnFinished(true);
		}
	}
}
