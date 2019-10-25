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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

/**
 * <p>处理系统的UI请求。</p>
 * 
 * <p>假设有n个任务在执行，一些任务需要UI交互，那么该类提供可以使用的UI交互界面。 </p>
 * 
 * <p>每个任务都应该有run、doUI等方法，当UI请求完毕，任务应该主动撤销请求。任务的请求界面可能会随时被切换，任务自己保存状态。</p>
 * 
 * <p>如果任务的UI是弹出窗口等，那么可以不通过此类。任务可以自行取消UI交互请求。</p>
 * 
 * 一般情况下UI请求并非是线程挂起的类型，UIManager也不阻塞任何线程，所有的线程和环境变量都有任务自己负责。 
 * 
 * @author Administrator
 *
 */
public class UIManager {
	public static final String SWT = "swt";
	public static final String SWING = "swing";
	
	/**
	 * 已注册的可以处理UI请求的UI事物。
	 */
	private static Map<String, UIHandler> uis = new HashMap<String, UIHandler>();
	
	/**
	 * 注册UI。
	 * 
	 * @param id
	 * @param ui
	 */
	public static void registUIHandler(String id, UIHandler ui){
		uis.put(id, ui);
	}
	
	/**
	 * 移除已注册的UI类型。
	 * 
	 * @param id
	 */
	public static void removeUIHandler(String id){
		uis.remove(id);
	}
	
	/**
	 * 根据类型获取一个UI。
	 * 
	 * @param id
	 * @return
	 */
	public static UIHandler getUIHandler(String id){
		return uis.get(id);
	}
	
	/**
	 * <p>请求UI，如果成功返回true，否则返回false。</p>
	 * 
	 * 其中ID是请求的事物自己生成的，一般会把事物的路径和ID一起作为一个唯一标识，这样为了避免重复氢气。
	 * 
	 * @param uiHandlerId
	 * @param thing
	 * @param requestId
	 * @param priority
	 * @param message
	 * @param actionContext
	 * @return
	 */
	public static boolean requestUI(String uiHandlerId, Thing thing, String requestId, int priority, Object message, ActionContext actionContext){
		UIRequest request = new UIRequest(thing, requestId, priority, actionContext);
		request.setUiHandlerId(uiHandlerId);
		request.setRequestMessage(message);
		
		return requestUI(uiHandlerId, request, actionContext);
	}
	
	/**
	 * 请求UI的事物发送请求，如果对应uiHandlerId的处理器存在，返回true，否则返回false。
	 * 
	 * @param uiHandlerId
	 * @param request
	 * @param actionContext
	 * @return
	 */
	public static boolean requestUI(String uiHandlerId, UIRequest request, ActionContext actionContext){
		UIHandler handler = uis.get(uiHandlerId);
		
		if(handler != null){			
			if(request.getUiHandlerId() == null){
				request.setUiHandlerId(uiHandlerId);
			}
			handler.requestUI(request, actionContext);
			return true;
		}else{
			return false;
		}
	}
		
	public static boolean requestUI(String uiHandlerId, Thing thing, String requestId, int priority, ActionContext actionContext){
		return requestUI(uiHandlerId, thing, requestId, priority, null, actionContext);
	}
	
	public static boolean requestUI(String uiHandlerId, Thing thing, String requestId, ActionContext actionContext){
		return requestUI(uiHandlerId, thing, requestId, 0, null, actionContext);
	}
	
	public static boolean requestUI(String uiHandlerId, Thing thing, ActionContext actionContext){
		return requestUI(uiHandlerId, thing, "", 0, null, actionContext);
	}
	
	public static boolean requestUI(UIHandler handler, Thing thing, String requestId, int priority, Object message, ActionContext actionContext){
		UIRequest request = new UIRequest(thing, requestId, priority, actionContext);
		request.setUiHandlerId(requestId);
		request.setRequestMessage(message);
		
		handler.requestUI(request, actionContext);
		return true;
	}
	
	public static boolean requestUI(UIHandler handler, Thing thing, String requestId, int priority, ActionContext actionContext){
		return requestUI(handler, thing, requestId, priority, null, actionContext);
	}
	
	public static boolean requestUI(UIHandler handler, Thing thing, String requestId, ActionContext actionContext){
		return requestUI(handler, thing, requestId, 0, null, actionContext);
	}
	
	public static boolean requestUI(UIHandler handler, Thing thing, ActionContext actionContext){
		return requestUI(handler, thing, "", 0, null, actionContext);
	}
	
	/**
	 * 返回所有UIHandler的标识列表。
	 * 
	 * @return
	 */
	public static List<String> getUIHandlerIds(){
		List<String> idList = new ArrayList<String>();
		idList.addAll(uis.keySet());
		
		Collections.sort(idList);
		return idList;
	}
}