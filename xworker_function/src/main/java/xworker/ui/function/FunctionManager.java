/*******************************************************************************
* Copyright 2007-2013 See AUTHORS file.
 * 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package xworker.ui.function;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.ui.UIRequest;

public class FunctionManager {
	/**
	 * 创建并发送一个请求参数设置的函数事物，返回值是新创建的设置参数的函数事物。
	 * 
	 * @param param
	 * @param actionContext
	 */
	public static void sendParameterRequest(FunctionParameter param, ActionContext actionContext){
		FunctionRequest functionRequest = param.getRequest();
		functionRequest.setFocusedParam(param);
		
		FunctionRequestUIFactory.updateRequestUI(functionRequest);
	}
	
	public static void sendRequest(FunctionRequest request, ActionContext actionContext){
		FunctionRequestUIFactory.updateRequestUI(request);
	}
	
	public static void finishRequest(FunctionRequest request, Object value){
		if(request.isCloseUIOnFinished()){
			FunctionRequestUIFactory.closeUI(request);
		}else{
			request.setFocusedParam(null);
			request.setValue(value);
			
			sendRequest(request, request.getActionContext());
			
			//显示函数执行完毕的界面
			UIRequest uiRequest = new UIRequest(World.getInstance().getThing("xworker.ui.function.FunctionResultShell/@composite"), request.getActionContext());
			
			//消息设置成FunctionRequest
			uiRequest.setRequestMessage(request);
			uiRequest.setFinishedMessage(value);
			
			//向界面发送请求
			FunctionRequestUIFactory.requestUI(request, "xworker_session_swt_dialogComposite", uiRequest, request.getActionContext());
			
			if(FunctionRequest.functionMonitor != null){
				FunctionRequest.functionMonitor.afterExecutedFunction(request);
			}
		}
	}
	
	public static void sendErrorRequest(FunctionRequest request, Throwable t){
		//显示函数执行完毕的界面
		UIRequest uiRequest = new UIRequest(World.getInstance().getThing("xworker.ui.function.FunctionErrorShell/@composite"), request.getActionContext());
		
		//消息设置成FunctionRequest
		uiRequest.setRequestMessage(request);
		uiRequest.setFinishedMessage(t);
		
		//向界面发送请求
		FunctionRequestUIFactory.requestUI(request, "xworker_session_swt_dialogComposite", uiRequest, request.getActionContext());
	}
	
	/**
	 * 发送编辑参数的请求。
	 * 
	 * @param parameter
	 */
	public static void sendEditParameterRequest(FunctionParameter parameter){
		FunctionRequest functionRequest = parameter.getRequest();
		functionRequest.setFocusedParam(parameter);
		
		FunctionRequestUIFactory.updateRequestUI(functionRequest);
	}
	
	public static void sendEditFunctionRequest(FunctionRequest request){
		FunctionRequestUIFactory.updateRequestUI(request);
	}

	public static void sendAddParameterRequest(FunctionRequest request){
		//构造编辑事物的请求
		Thing requestThing = World.getInstance().getThing("xworker.ui.function.xworker.AddParameterRequest");		
		UIRequest uiRequest = new UIRequest(requestThing, request.getActionContext());
		
		//要编辑的事物
		uiRequest.setRequestMessage(request);
		
		//发送编辑的请求
		FunctionRequestUIFactory.requestUI(request, "xworker_session_swt_dialogForm", uiRequest, request.getActionContext());
	}
}