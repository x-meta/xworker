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

import xworker.ui.UIManager;
import xworker.ui.UIRequest;

public class FunctionCallback {
	/** 函数回调的参数 */
	FunctionRequest request;
	FunctionParameter param;
	
	/** UI请求回调的参数 */
	UIRequest uiRequest;
	String uiHandlerId;
	
	/** 线程锁定回调 */
	Object lockObj;
	
	/**
	 * 线程锁定回调，其他线程正在等待本回调激活线程。
	 */
	public FunctionCallback(Object lockObj){
		this.lockObj = lockObj;
	}
	
	/**
	 * 参数的回调。
	 * @param param
	 */
	public FunctionCallback(FunctionParameter param){
		this.request = param.getRequest();
		this.param = param;
	}
	
	/**
	 * 函数请求的回调。
	 * 
	 * @param request
	 */
	public FunctionCallback(FunctionRequest request){
		this.request = request;
		this.param = null;
	}
	
	public FunctionCallback(UIRequest uiRequest, String uiHandlerId){
		this.uiHandlerId = uiHandlerId;
		this.uiRequest = uiRequest;
	}
	
	/**
	 * 参数设置完毕，并返回到调用函数的界面。
	 * 
	 * @param value
	 * @param actionContext
	 */
	public void ok(Object value, ActionContext actionContext){
		ok(value, FunctionStatus.RUNNING, actionContext);
		//FunctionManager.sendRequest(request, actionContext);
	}
	
	public void ok(Object value, FunctionStatus status, ActionContext actionContext){
		if(request != null){
			if(FunctionRequest.functionMonitor != null){
				FunctionRequest.functionMonitor.afterExecutedParameter(request, param);
			}
			if(param == null){
				//可能是函数调用函数等情形
				request.value = value;				
				FunctionRequestUtil.callbakMyselfOk(request, value, actionContext);
			}else{
				request.setStatus(status);
				request.setParameterValue(param.getName(), value);
				
				param.setTrustChecked(true);//参数已经可信任
				request.focusedParam = param;
				
				FunctionAssist assist = request.getFunctionAssist();
				if(assist.isStopCallback(param.getValueRequest(), actionContext)){
					return;
				}else if(assist.getDebugStatus() == FunctionAssist.DEBUG_STEP_RETURN && request.parent != null){ //不能用getParent
					FunctionManager.sendRequest(request.parent, actionContext);
					assist.stepNone();
				}else{
					//普通的正常执行
					request.callbackRun(param, actionContext);
				}
			}
		}else if(uiHandlerId != null){
			UIManager.requestUI(uiHandlerId, uiRequest, uiRequest.getActionContext());
		}else if(lockObj != null){
			synchronized(lockObj){
				lockObj.notify();
			}
		}
	}
	
	/**
	 * 参数设置取消，并返回原始界面。
	 * 
	 * @param actionContext
	 */
	public void cancel(ActionContext actionContext){
		cancel(null, actionContext);
	}
	
	public void cancel(FunctionStatus status, ActionContext actionContext){
		if(request != null){
			if(FunctionRequest.functionMonitor != null){
				FunctionRequest.functionMonitor.afterExecutedParameter(request, param);
			}
			
			if(param == null){				
				if(request.getCallback() != null){
					request.getCallback().cancel(actionContext);
				}else{
					FunctionManager.sendRequest(request, actionContext);
				}
			}else{
				if(status != null){
					request.setStatus(status);
				}
				request.focusedParam = param;
				FunctionManager.sendRequest(request, actionContext);
			}
		}else if(uiHandlerId != null){
			UIManager.requestUI(uiHandlerId, uiRequest, uiRequest.getActionContext());
		}else if(lockObj != null){
			synchronized(lockObj){
				lockObj.notify();
			}
		}
	}

	public FunctionRequest getRequest() {
		return request;
	}

	public FunctionParameter getParam() {
		return param;
	}

}