package xworker.ui.function.functionUtils;

import org.eclipse.swt.widgets.Event;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.ui.UIRequest;
import xworker.ui.function.FunctionAssist;
import xworker.ui.function.FunctionCallback;
import xworker.ui.function.FunctionManager;
import xworker.ui.function.FunctionParameter;
import xworker.ui.function.FunctionRequest;
import xworker.ui.function.FunctionRequestUIHandlerProxy;
import xworker.ui.function.FunctionRequestUtil;

public class SelectBuestFunction {
	public static void setBestFunction(ActionContext actionContext){
		UIRequest uiRequest = (UIRequest) actionContext.get("request");
		Thing thing = (Thing) actionContext.get("result");

		
		if(thing != null){
			//如果是函数调用，返回并设置上层的调用者的函数
			FunctionRequest functionRequest = (FunctionRequest) uiRequest.getRequestMessage();
			Event event = (Event) actionContext.get("event");
			boolean edit = false;
			if(event != null && event.widget == actionContext.get("okAndEditButton")){
				edit = true;
			}
			FunctionRequest fnRequest = functionRequest;
			if(fnRequest.getCallback() != null && fnRequest.getCallback().getRequest().getParent() != null){
				fnRequest = fnRequest.getCallback().getRequest().getCallback().getRequest();
			}
			
			Thing valueThing = new Thing(thing.getMetadata().getPath());
			if(fnRequest.getCallback() == null){
				valueThing.put("name", fnRequest.getThing().getMetadata().getName());
				fnRequest.init(valueThing, false);
				
				FunctionManager.sendRequest(functionRequest, actionContext);
				
				if(!edit){
					fnRequest.run(fnRequest.getActionContext());
				}
				return;
			}else{
				FunctionRequest parentRequest = fnRequest.getCallback().getRequest();
				FunctionParameter parameter = fnRequest.getCallback().getParam();
				valueThing.put("name", parameter.getName());
				parameter.setValueThing(valueThing, null);
				parentRequest.setFocusedParam(parameter);
				FunctionParameter unReadyParam = parameter.getValueRequest().getFirstUnReadyParam();
				if(unReadyParam != null){
					//如果新的参数的函数还有参数需要设置，那么设置参数
					FunctionManager.sendParameterRequest(unReadyParam, actionContext);
				}else{
					//先刷新函数树
					FunctionManager.sendRequest(functionRequest, actionContext);
					if(!edit){
						//再执行新的函数					
						parameter.run();
					}
				}
			}
		}
	}
	
	public static void executeBestFunction(ActionContext actionContext){
		UIRequest uiRequest = (UIRequest) actionContext.get("request");
		FunctionRequest request = (FunctionRequest) uiRequest.getRequestMessage();
		Thing functionThing = (Thing) actionContext.get("result");
		
		FunctionRequest childFunctionRequest = request.getChildFunctionRequest();		
		if(functionThing != null){
			//functionThing = new Thing(functionThing.getMetadata().getPath());
			//应该每次都设置子函数
			childFunctionRequest = new FunctionRequest(functionThing, true, actionContext);
			FunctionCallback callback = new FunctionCallback(request);		
			childFunctionRequest.setParent(request);		
			childFunctionRequest.setCallback(callback);
			childFunctionRequest.setUiKey(request.getUiKey()); //UI使用同一个
			request.setChildFunctionRequest(childFunctionRequest);
			
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
			FunctionRequestUtil.callbakMyselfOk(request, null, actionContext);
		}
	}
	
	/**
	 * 执行选择的最佳函数的方法。
	 * 
	 * @param actionContext
	 */
	public static void executeBestFunctionDoFucntion(ActionContext actionContext){
		//如果已经有子函数，那么说明是子函数的回调，否者应该进入选择函数的界面
		FunctionRequestUIHandlerProxy.run(actionContext);
	}
}
