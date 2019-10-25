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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

/**
 * UI请求的监控器。
 * 
 * @author Administrator
 *
 */
public class UIMonitor {
	/** UI监控器的定义事物 */
	Thing thing;
	
	/** UI请求列表 */
	Map<String, List<UIRequest>> requestMap = new HashMap<String, List<UIRequest>>();
	
	/** 当前正在执行的UIRequest。 */
	UIRequest currentRequest = null;
		
	public UIMonitor(Thing thing){
		this.thing = thing;
	}
		
	/**
	 * 发生了UI请求。
	 * 
	 * @param uiHandlerId
	 * @param request
	 */
	public synchronized void requestStarted(String uiHandlerId, UIRequest request){
		request.setUiHandlerId(uiHandlerId);
		
		List<UIRequest> requests = getRequestList(uiHandlerId);
		if(!requests.contains(request)){
			requests.add(request);
		}
		
		doNextUIRequest(uiHandlerId);
	}
	
	/**
	 * UI请求结束了。
	 * 
	 * @param request
	 */
	public synchronized void requestFinished(UIRequest request){
		List<UIRequest> requests = getRequestList(request.getUiHandlerId());
		
		requests.remove(request);
		if(request == currentRequest){
			currentRequest = null;
		}
		
		doNextUIRequest(request.getUiHandlerId());
	}
	
	private List<UIRequest> getRequestList(String uiHandlerId){
		List<UIRequest> reqs = requestMap.get(uiHandlerId);
		if(reqs == null){
			reqs = new ArrayList<UIRequest>();
			requestMap.put(uiHandlerId, reqs);
		}
		
		return reqs;
	}
	
	private void doNextUIRequest(String uiHandlerId){
		List<UIRequest> requests = getRequestList(uiHandlerId);
		if(currentRequest == null && requests.size() >0){
			doUIRequest(requests.get(0));
		}
	}
	
	private void doUIRequest(UIRequest request){
		List<UIRequest> requests = getRequestList(request.getUiHandlerId());
		UIHandler handler = UIManager.getUIHandler(request.getUiHandlerId());
		if(handler == null){
			handler =  startUIHandler(request.getUiHandlerId());
		}
		
		if(handler != null){
			currentRequest = request;
			handler.requestUI(request, request.getActionContext());			
		}else{
			//请求无法处理，先移除，以后有需要再想其他办法
			requests.remove(request);
			
			doNextUIRequest(request.getUiHandlerId());
		}
	}
	
	/**
	 * 启动相关的UIHandler。
	 */
	public UIHandler startUIHandler(String uiHandlerId){
		return (UIHandler) thing.doAction("startUIHandler", new ActionContext(), UtilMap.toMap(new Object[]{"uiHandlerId", uiHandlerId}));
	}
}