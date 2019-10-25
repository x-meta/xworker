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

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;

public class UIActions {
	/**
	 * UI请求。
	 * 
	 * @param actionContext
	 * @return
	 * @throws OgnlException 
	 */
	public static boolean requestUI(ActionContext actionContext) throws OgnlException{
		UIHandler uiHandler = UIActions.getUIHandler(actionContext);
		
		if(uiHandler == null){
			return false;
		}else{
			UIRequest request = UIActions.getUIRequest(actionContext, true);
			uiHandler.requestUI(request, actionContext);
			return true;
		}	
	}
	
	public static UIHandler getUIHandler(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		UIHandler uiHandler = null;
		String uiHandlerVarName = self.getStringBlankAsNull("uiHandlerVarName");
		if(uiHandlerVarName != null){
			uiHandler = (UIHandler) OgnlUtil.getValue(self, "uiHandlerVarName", uiHandlerVarName, actionContext);
		}
		
		if(uiHandler == null){
			String uiHandlerId = self.getStringBlankAsNull("uiHandlerId");
			if(uiHandlerId == null){
				throw new ActionException("RequestUI: uiHandlerId cann't be null, thing=" + self, actionContext);
			}
			
			uiHandler = UIManager.getUIHandler(uiHandlerId);
		}
			
		return uiHandler;		
	}
	
	public static UIRequest getUIRequest(ActionContext actionContext, boolean isRequest) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		//首先从变量中取
		String uiRequest = self.getStringBlankAsNull("uiRequest");
		if(uiRequest != null){
			UIRequest request = (UIRequest) OgnlUtil.getValue(self, "uiRequest", uiRequest, actionContext);
			if(request == null){
				throw new ActionException("RequestUI: ui request can not be null, thing=" + self, actionContext);				
			}else{
				return request;
			}
		}
		
		//其次是创建一个UIRequest
		Thing thing = World.getInstance().getThing(self.getString("thingPath"));
		if(thing == null){
			String thingPathVar = self.getStringBlankAsNull("thingPathVar");
			if(thingPathVar != null){
				thing = World.getInstance().getThing((String) OgnlUtil.getValue(self, "thingPathVar",thingPathVar, actionContext));
			}
		}
		
		if(thing == null){
			throw new ActionException("RequestUI: request thing can not be null, thing=" + self, actionContext);
		}
		
		String requestId = self.getString("requestId");
		int priority = self.getInt("priority");
		UIRequest request = new UIRequest(thing, requestId, priority, actionContext);
		
		//设置message
		Object message = null;
		String messageVarName = self.getStringBlankAsNull("messageVarName");
		if(messageVarName != null){
			message = OgnlUtil.getValue(thing, "messageVarName", messageVarName, actionContext);
		}
		if(message == null){
			message = self.getString("message");
		}
		
		if(isRequest){
			request.setRequestMessage(message);
		}else{
			request.setFinishedMessage(message);
		}
		return request;
	}
	/**
	 * 结束UI请求。
	 * 
	 * @param actionContext
	 * @return
	 * @throws OgnlException 
	 */
	public static boolean finishUI(ActionContext actionContext) throws OgnlException{
		UIHandler uiHandler = UIActions.getUIHandler(actionContext);
		
		if(uiHandler == null){
			return false;
		}else{
			UIRequest request = UIActions.getUIRequest(actionContext, false);
			uiHandler.finishUI(request, actionContext);
			return true;
		}		
	}
}