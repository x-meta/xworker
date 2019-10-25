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
package xworker.ui;

import java.util.HashMap;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

/**
 * 具有通用基本功能实现的UIRequestHandler。
 * 
 * @author Administrator
 *
 */
public abstract class AbstractUIHandler implements UIHandler{

	/** 定义UIHandler的事物 */
	protected Thing thing;
	
	/** uiHandler的标识 */
	protected String uiHandlerId;
	
	/** 是否要调用自己当callback时 */
	private boolean callbakMyself = false;
	
	/** 创建该UIHandler是的变量上下文 */
	protected ActionContext actionContext;
		
	public AbstractUIHandler(Thing thing, String uiHandlerId, ActionContext actionContext){
		this.thing = thing;
		this.uiHandlerId = uiHandlerId;
		this.callbakMyself = thing.getBoolean("callbakMyself");
		this.actionContext = actionContext;
	}
	
	@Override
	public Thing getThing() {
		return thing;
	}
	
	public String getUiHandlerId() {
		return uiHandlerId;
	}

	/**
	 * 请求时的回调，如果请求设置requestCallback=true，调用请求事物的requestCallback方法。
	 * 
	 * @param request
	 * @param params
	 */
	protected void requestCallback(UIRequest request, Map<String, Object> params){
		callback(request, params, "requestCallback", request.isRequestCallback());
	}
	
	/**
	 * 结束时的回调，如果请求设置finishCallback=true，调用请求事物的finishCallback方法。
	 * 
	 * @param request
	 * @param params
	 */
	protected void finishCallback(UIRequest request, Map<String, Object> params){
		callback(request, params, "finsihCallback", request.isFinishCallback());
	}
	
	private void callback(UIRequest request, Map<String, Object> params, String method, boolean requestCallback){
		if(requestCallback || this.callbakMyself){
			if(params == null && request.getThing() != null){
				params = new HashMap<String, Object>();
			}
			
			params.put("uiRequest", request);
			params.put("uiHandler", this);
			
			if(callbakMyself){
				thing.doAction(method, actionContext, params);
			}
			
			if(requestCallback){
				request.getThing().doAction(method, request.getActionContext(), params);
			}
		}
	}

	public ActionContext getActionContext() {
		return actionContext;
	}
}