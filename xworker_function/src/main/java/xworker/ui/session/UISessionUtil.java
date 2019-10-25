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
package xworker.ui.session;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.ui.UIManager;
import xworker.ui.UIRequest;

public class UISessionUtil {
	/**
	 * 发送一个自定义面板的请求，其中composite用于生成界面，在界面的变量上下文中传入request，request.getRequestMessage()是传入的消息。
	 * 
	 * @param composite
	 * @param requestMessage
	 * @param actionContext
	 */
	public static void sendCompositeRequest(Thing composite, Object requestMessage, ActionContext actionContext){
		//构造一个uiRequest
		UIRequest uiRequest = new UIRequest(composite, actionContext);
		
		//消息设置成FunctionRequest
		uiRequest.setRequestMessage(requestMessage);
		
		//向界面发送请求
		UIManager.requestUI("xworker_session_swt_dialogComposite", uiRequest, actionContext);
	}
	
	/**
	 * 发送编辑事物的请求。
	 * 
	 * @param requestThing   请求编辑的事物
	 * @param forEdit        要编辑的事物
	 * @param actionContext
	 */
	public static void sendEditThingRequest(Thing requestThing, Thing forEdit, ActionContext actionContext){
		Thing composite = World.getInstance().getThing("xworker.ui.session.manager.xworker.ThingEditor/@composite");

	}
}