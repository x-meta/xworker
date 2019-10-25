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

import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilMap;

import xworker.ui.UIRequest;

public class FunctionRequestUIHandlerProxy {
	//private static Logger logger = LoggerFactory.getLogger(FunctionRequestUIHandlerProxy.class);
	
	public static void run(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");		
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		if(request == null){
			throw new ActionException("function is running at quiet mode, no ui, path=" + self.getMetadata().getPath());
		}
		
		//获取真正的函数定义
		self = self.getDescriptor();
		if("thing".equals(self.getThingName()) &&  self.getExtends().size() > 0){
			self = self.getExtends().get(0);
		}
		String uiHandlerId = self.getStringBlankAsNull("uiHandlerId");
		String uiThingPath = self.getStringBlankAsNull("uiThing");
		
		Thing uiThing = null;
		if(uiThingPath != null){
			uiThing = World.getInstance().getThing(uiThingPath);
		}
		if(uiThing == null){
			uiThing = request.getDescriptor().getThing("Composite@0");
		}
		
		if(uiHandlerId == null || uiThing == null){
			throw new ActionException("ui thing or uiHandlerId is null");
		}else{
			UIRequest uiRequest = new UIRequest(uiThing, actionContext);
			//proxy应该是其他函数的被调用者
			uiRequest.setRequestMessage(request);
			uiRequest.setRequestCallback(true);
			FunctionRequestUIFactory.requestUI(request, uiHandlerId, uiRequest, actionContext);

			//显示outline的文档
			FunctionRequestUtil.initFunctionRequestHtml(request, null, actionContext);
		}
	}
	
	public static void formRun(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");		
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		
		self = self.getDescriptor();
		if(self.getExtends().size() > 0){
			self = self.getExtends().get(0);
		}
		String uiHandlerId = self.getStringBlankAsNull("uiHandlerId");		
		if(uiHandlerId == null){
			
		}else{
			UIRequest uiRequest = new UIRequest(World.getInstance().getThing("xworker.ui.function.swt.FunctionFormUIProxyShell/@formComposite"), actionContext);
			//proxy应该是其他函数的被调用者
			uiRequest.setRequestMessage(request);
			uiRequest.setRequestCallback(true);
			FunctionRequestUIFactory.requestUI(request, uiHandlerId, uiRequest, actionContext);

			//显示outline的文档
			FunctionRequestUtil.initFunctionRequestHtml(request, null, actionContext);
		}
	}
	
	/**
	 * form请求表单的初始化。
	 * 
	 * @param actionContext
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void formCompoisteInit(ActionContext actionContext){
		UIRequest uiRequest = (UIRequest) actionContext.get("request");
		FunctionRequest fnRequest = (FunctionRequest) uiRequest.getRequestMessage();
		Thing descThing = fnRequest.getDescriptor();
		
		//Composite formComposite = (Composite) actionContext.get("formComposite");
		
		String formPath = descThing.getStringBlankAsNull("formThingPath");
		Thing formThing = null;
		if(formPath != null){
			formThing = World.getInstance().getThing(formPath);
		}
		
		if(formThing == null){
			formThing = descThing.getThing("FormThing@0");			
		}
		
		if(formThing != null){
			Thing form = (Thing) actionContext.get("form");
			
			Thing editThing = new Thing(formThing.getMetadata().getPath());
			editThing.initDefaultValue();
			
			Object value = fnRequest.getValue();
			if(value == null){
				FunctionParameter pdefault = fnRequest.getParameter("default");
				if(pdefault != null){
					value = pdefault.getValue();
				}
			}
			if(value == null){
				//默认值
				value = fnRequest.getThing().getStringBlankAsNull("default");
			}
			if(value != null){
				String attributeName = descThing.getStringBlankAsNull("returnAttributeName");
				if(attributeName != null){
					editThing.set(attributeName, value);
				}else if(value instanceof Map){
					editThing.getAttributes().putAll(((Map) value));
				}
			}
			
			form.doAction("setThing", actionContext, UtilMap.toMap(new Object[]{"thing", editThing}));
		}
	}
}