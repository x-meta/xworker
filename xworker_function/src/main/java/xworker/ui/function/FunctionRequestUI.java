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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.ui.UIHandler;
import xworker.ui.UIRequest;

public abstract class FunctionRequestUI {
	private static Logger logger = LoggerFactory.getLogger(FunctionRequestUI.class);
	
	/** UIHandler的缓存 */
	protected Map<String, UIHandler> handlers = new HashMap<String, UIHandler>();
	
	/**
	 * 请求界面。
	 * 
	 * @param functionRequest
	 * @param uiHandlerId
	 * @param requestThing
	 * @param actionContext
	 */
	public void requestUI(FunctionRequest functionRequest, String uiHandlerId, Thing requestThing, ActionContext actionContext){
		UIRequest request = new UIRequest(requestThing, null, 0, actionContext);
		request.setUiHandlerId(uiHandlerId);
		request.setRequestMessage(new FunctionCallback(functionRequest));
		
		requestUI(uiHandlerId, request, actionContext);
	}
	
	public void requestUI(String uiHandlerId, UIRequest uiRequest, ActionContext actionContext){
		UIHandler handler = handlers.get(uiHandlerId);
		if(handler != null){
			handler.requestUI(uiRequest, actionContext);
			forceActive();
		}else{
			logger.info("UIhander not exists, uiHandlerId=" + uiHandlerId);
		}
	}
	
	public UIHandler getUIHandler(String uiHandlerId){
		return handlers.get(uiHandlerId);
	}
	
	public void putUIHandler(String uiHandlerId, UIHandler handler){
		handlers.put(uiHandlerId, handler);
	}
	
	/**
	 * 创建UI，初始化时会调用的动作。
	 * 
	 * @param functionRequest
	 */
	public abstract void createUI(FunctionRequest functionRequest);
	
	/**
	 * 强制在界面上显示。
	 * 
	 */
	public abstract void forceActive();
	
	/**
	 * 更新请求的UI。
	 * 
	 * @param functionRequest
	 */
	public abstract void updateRequestUI(FunctionRequest functionRequest);
	
	/**
	 * 关闭UI。
	 * 
	 */
	public abstract void close();
}