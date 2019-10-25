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
package xworker.ui.function.actions;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.OgnlUtil;

import ognl.OgnlException;
import xworker.ui.UIRequest;
import xworker.ui.function.FunctionCallback;
import xworker.ui.function.FunctionRequest;
import xworker.ui.function.FunctionRequestUIFactory;

public class FunctionActions {
	public static void sendFunctionUIRequest(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String functionVarName = self.getString("functionVarName");
		FunctionRequest functionRequest = (FunctionRequest) actionContext.get(functionVarName);
		
		UIRequest uiRequest = new UIRequest(World.getInstance().getThing(self.getString("uiThingPath")), actionContext);
		uiRequest.setRequestMessage(functionRequest);
		
		String uiHandlerId = self.getStringBlankAsNull("uiHandlerId");
		if(uiHandlerId == null){
			uiHandlerId = "xworker_session_swt_dialogComposite";
		}
		FunctionRequestUIFactory.requestUI(functionRequest, uiHandlerId, uiRequest, actionContext);
	}
	
	public static void sendFunctionRequest(ActionContext actionContext) throws OgnlException{
		Thing self = (Thing) actionContext.get("self");
		
		//回调，可以为空
		FunctionRequest callbackRequest = null;
		String callbakRequestVarName= self.getStringBlankAsNull("callbakRequestVarName");
		String callbackParam = self.getStringBlankAsNull("callbackParam");
		
		if(callbakRequestVarName != null){
			callbackRequest = (FunctionRequest) OgnlUtil.getValue(self, "callbakRequestVarName", callbakRequestVarName, actionContext);
			
		}
		
		//确定函数
		Thing function = null;
		String functionVarName = self.getStringBlankAsNull("functionVarName");
		if(functionVarName != null){
			function = (Thing) OgnlUtil.getValue(self, "functionVarName", functionVarName, actionContext);
		}
		
		if(function == null){
			String functionPath = self.getStringBlankAsNull("functionPath");
			if(functionPath != null){
				function = World.getInstance().getThing(functionPath);
			}
		}
		
		if(function == null){
			String descriptor = null;
			String functionDescriptorVarName = self.getStringBlankAsNull("functionDescriptorVarName");
			if(functionDescriptorVarName != null){
				descriptor = (String) OgnlUtil.getValue(self, "functionDescriptorVarName", functionDescriptorVarName, actionContext);
			}
			
			if(descriptor == null){
				descriptor = self.getStringBlankAsNull("functionDescriptorPath");
			}
			
			if(descriptor != null){
				function = new Thing(descriptor);
			}
		}
		
		if(function == null){
			throw new ActionException("SendFunctionRequest: can not send function request, function is null, thing=" + self.getMetadata().getPath());
		}
		
		FunctionRequest request = null;
		if(callbackRequest != null && callbackParam != null){
			FunctionCallback callback = new FunctionCallback(callbackRequest.getParameter(callbackParam));
			request = new FunctionRequest(function, callback, actionContext);
		}else{
			request = new FunctionRequest(function, actionContext);
		}
		
		request.run(actionContext);
		//FunctionManager.sendRequest(request, actionContext);
	}
}