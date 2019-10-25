package xworker.ui.function.common;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.ui.function.FunctionAssist;
import xworker.ui.function.FunctionCallback;
import xworker.ui.function.FunctionManager;
import xworker.ui.function.FunctionQuietRunner;
import xworker.ui.function.FunctionRequest;

public class CallFunctionActions {
	/**
	 * 执行由函数定义的函数。
	 * 
	 * @param actionContext
	 */
	public static Object callFunction(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");		
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		
		//寻找要执行的函数
		String functionPath = (String) actionContext.get("functionPath");		
		Thing functionThing = World.getInstance().getThing(functionPath);
		
		if(request == null){
			return FunctionQuietRunner.runFunction(functionThing, actionContext);
		}else{
			FunctionRequest childFunctionRequest = request.getChildFunctionRequest();		
			if(functionThing != null){
				//如果子函数没有设置，设置子函数
				if(childFunctionRequest == null || childFunctionRequest.getThing() != functionThing){
					childFunctionRequest = new FunctionRequest(functionThing, actionContext);
					FunctionCallback callback = new FunctionCallback(request);		
					childFunctionRequest.setParent(request);		
					childFunctionRequest.setCallback(callback);
					childFunctionRequest.setUiKey(request.getUiKey()); //UI使用同一个
					request.setChildFunctionRequest(childFunctionRequest);
				}
				
				childFunctionRequest.getFunctionAssist().stepNone();
				if(request.getFunctionAssist().getDebugStatus() == FunctionAssist.DEBUG_STEP_INTO){
					request.getFunctionAssist().stepNone();
					childFunctionRequest.setFocusedParam(null);
					FunctionManager.sendRequest(childFunctionRequest, actionContext);
				}else{
					childFunctionRequest.getFunctionAssist().stepNone();
					childFunctionRequest.run(actionContext);
				}
			}else{
				//FunctionRequestUtil.callbakMyselfOk(request, null, actionContext);
				throw new ActionException("function thing not exists, callfunction=" + self.getMetadata().getPath());
			}
			
			return null;
		}
	}

}
