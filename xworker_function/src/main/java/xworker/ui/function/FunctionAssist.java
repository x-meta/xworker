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

import java.util.List;

import org.xmeta.ActionContext;

/**
 * 函数的辅助工具。
 * 
 * @author Administrator
 *
 */
public class FunctionAssist {
	public static final byte DEBUG_STEP_OVER = 1;
	public static final byte DEBUG_STEP_RETURN = 2;
	public static final byte DEBUG_STEP_INTO = 3;
	public static final byte DEBUG_NONE = 0;
	public static final byte STEP_SET = 1;
	public static final byte STEP_CALLBACK = 2;
	public static final byte STEP_NONE = 0;
	
	byte debugStauts = 0;
	byte step = 0;
	FunctionRequest debugStatusSetter;
	
	public byte getDebugStatus(){
		return this.debugStauts;
	}	
	
	public void stepInto(FunctionRequest debugStatusSetter){
		this.debugStauts = DEBUG_STEP_INTO;		
		this.debugStatusSetter = debugStatusSetter;
	}
	
	public void stepOver(FunctionRequest request, ActionContext actionContext){
		if(debugStatusSetter != request || step == 0){
			this.debugStauts = DEBUG_STEP_OVER;
			this.step = STEP_SET;
			this.debugStatusSetter = request;
			request.run(actionContext);
		}else{			
			FunctionCallback callback = request.getCallback();
			if(callback != null){
				callback.ok(callback.getParam().getValue(), actionContext);
			}else{
				this.debugStauts = DEBUG_NONE;
				this.step = STEP_NONE;
				this.debugStatusSetter = null;
			}
		}		
	}
	
	
	public void stepReturn(FunctionRequest debugStatusSetter){
		this.debugStauts = DEBUG_STEP_RETURN;
		this.debugStatusSetter = debugStatusSetter;
	}
	
	public void stepNone(){
		this.debugStauts = DEBUG_NONE; 
		this.debugStatusSetter = null;
	}

	public boolean isStop(FunctionRequest request, FunctionParameter lastParameter, ActionContext actionContext){
		if(request != debugStatusSetter &&  debugStauts == DEBUG_STEP_OVER){
			
			if(lastParameter != null){
				//如果lastParameter是函数的request的参数，并且是最后一个，那么在request上停止
				List<FunctionParameter> params = request.getParameters();
				if(params.size() > 0 && params.get(params.size() - 1) == lastParameter){
					//当做父函数的step_over
					debugStatusSetter = request;
					this.step = STEP_SET;
					request.setFocusedParam(null);
					request.callbackRun(lastParameter, actionContext);
					//FunctionManager.sendRequest(request, actionContext);					
					return true;
				}else{
					return false;
				}
			}else{
				request.setFocusedParam(null);
				FunctionManager.sendRequest(request, actionContext);
				step = STEP_NONE;
				return true;
			}
		}else{
			return false;
		}
	}
	
	public boolean isStopCallback(FunctionRequest request, ActionContext actionContext){
		if(step == STEP_SET && request == debugStatusSetter &&  debugStauts == DEBUG_STEP_OVER){
			request.setFocusedParam(null);
			FunctionManager.sendRequest(request, actionContext);
			step = STEP_CALLBACK;
			return true;
		}else if(step == STEP_CALLBACK && debugStatusSetter != null && debugStatusSetter.getCallback() != null && debugStatusSetter.getCallback().getRequest() == request){
			//判断最后一个参数执行完要停止在上级函数上
			FunctionParameter param = debugStatusSetter.getCallback().getParam(); 
			if(param != null){
				List<FunctionParameter> params = request.getParameters();
				if(params.get(params.size() - 1) == param){
					debugStatusSetter = request;
					request.setFocusedParam(null);
					FunctionManager.sendRequest(request, actionContext);
					return true;
				}else{
					return false;
				}
			}else{
				return false;
			}
		}else if(step == STEP_CALLBACK && debugStatusSetter != null && debugStatusSetter.getCallback() != null && debugStatusSetter.getCallback().getParam() == null){
			debugStatusSetter = request;
			request.setFocusedParam(null);
			FunctionManager.sendRequest(request, actionContext);
			return true;
		}else{
			
			return false;
		}
	}
}