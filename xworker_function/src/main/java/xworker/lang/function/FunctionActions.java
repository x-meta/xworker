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
package xworker.lang.function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.ui.function.FunctionAssist;
import xworker.ui.function.FunctionCallback;
import xworker.ui.function.FunctionManager;
import xworker.ui.function.FunctionQuietRunner;
import xworker.ui.function.FunctionRequest;
import xworker.ui.function.FunctionRequestUI;
import xworker.ui.function.FunctionRequestUIFactory;
import xworker.ui.function.FunctionRequestUtil;
import xworker.ui.function.uiimpls.DesignUI;
import xworker.ui.function.uiimpls.DialogUI;
import xworker.util.XWorkerUtils;

public class FunctionActions {
	private static Logger logger = LoggerFactory.getLogger(FunctionActions.class);
	
	/**
	 * 函数的run方法。
	 * 
	 * @param actionContext
	 */
	public static void runFunction(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		FunctionRequest request = new FunctionRequest(self, null, false, new ActionContext());
		request.run(actionContext);
	}
	
	public static Thing getSavedValueThing(ActionContext actionContext){
		String name = (String) actionContext.get("name");
		Object value = actionContext.get("value");
		
		return FunctionRequestUtil.getValueThing(name, value);
	}
	
	/**
	 * 静默执行函数。
	 * 
	 * @param actionContext
	 */
	public static Object runQuiet(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		return FunctionQuietRunner.runFunction(self, actionContext);
	}
	
	/** 
	 * 以对话框的形式执行。
	 * 
	 * @param actionContext
	 */
	public static void runDialog(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		FunctionRequest request = new FunctionRequest(self, null, false, new ActionContext());
		
		FunctionRequestUI ui = new DialogUI();
		FunctionRequestUIFactory.registUI(request, ui);
		ui.createUI(request);		
		
		request.run(actionContext);
	}
	
	/**
	 * 执行由函数定义的函数。
	 * 
	 * @param actionContext
	 */
	public static Object runFunctionFunction(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");		
		FunctionRequest request = (FunctionRequest) actionContext.get("request");
		
		//寻找要执行的函数
		Thing descriptor = null;
		Thing functionThing = null;
		for(Thing desc : self.getDescriptors()){
			if(desc.getThingName().equals("FunctionFunction")){
				descriptor = desc;
				break;
			}
			
			//也从继承上获取
			if(desc.getExtends().size() > 0){
				Thing ext = desc.getExtends().get(0);
				if(ext.getThingName().equals("FunctionFunction")){
					descriptor = ext;
					break;
				}
			}
		}
		if(descriptor != null){
			functionThing = descriptor.getThing("Function@0");
		}
		
		if(request == null){
			//静默执行的方式
			if(functionThing != null && functionThing.getChilds().size() > 0){
				return FunctionQuietRunner.runFunction(functionThing.getChilds().get(0), actionContext);
			}
			
			return null;
		}else{
			FunctionRequest childFunctionRequest = request.getChildFunctionRequest();		
			if(functionThing != null && functionThing.getChilds().size() > 0){
				//如果子函数没有设置，设置子函数
				if(childFunctionRequest == null || childFunctionRequest.getThing() != functionThing.getChilds().get(0)){
					childFunctionRequest = new FunctionRequest(functionThing.getChilds().get(0), actionContext);
					FunctionCallback callback = new FunctionCallback(request);		
					childFunctionRequest.setParent(request);		
					childFunctionRequest.setCallback(callback);
					childFunctionRequest.setUiKey(request.getUiKey()); //UI使用同一个
					request.setChildFunctionRequest(childFunctionRequest);
				}
				
				childFunctionRequest.getFunctionAssist().stepNone();
				if(request.getFunctionAssist().getDebugStatus() == FunctionAssist.DEBUG_STEP_INTO){
					request.getFunctionAssist().stepNone();
					childFunctionRequest.setFocusedParam(null);
					FunctionManager.sendRequest(childFunctionRequest, actionContext);
				}else{
					childFunctionRequest.getFunctionAssist().stepNone();
					childFunctionRequest.run(actionContext);
				}
			}else{
				FunctionRequestUtil.callbakMyselfOk(request, null, actionContext);
			}
			
			return null;
		}
	}
	
	/**
	 * 调用另外一个函数。
	 * 
	 * @param functionRequest
	 * @param actionContext
	 */
	public void callFunction(FunctionRequest functionRequest, ActionContext actionContext){
		
	}
	
	public static String doCheck(ActionContext actionContext){
		return null;
	}
	
	/**
	 * 获取参数的值，只返回第一个子节点的值。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Object runParameter(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		//如果paramVarName不为空表示取actionContext中的变量
		String paramVarName = self.getStringBlankAsNull("paramVarName");
		if(paramVarName != null){
			return actionContext.get(paramVarName);
		}
		
		//第一个子节点应该是函数，获取函数的值
		if(self.getChilds().size() > 0){
			return self.getChilds().get(0).doAction("run", actionContext);
		}else{
			return null;
		}
	}
	
	/**
	 * 执行函数动作。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Object runFunctionAction(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		Thing function = self.doAction("getFunction", actionContext);
		String uiMode = self.doAction("getUI", actionContext);
		
		if(function == null && "noui".equals(uiMode)){
			logger.warn("NoUI mode, funtion not setted, path=" + self.getMetadata().getPath());
			return null;
		}
		
		String requestRenameTo = self.getStringBlankAsNull("requestRenameTo");
		if(requestRenameTo != null){
			actionContext.peek().put(requestRenameTo, actionContext.get("request"));
		}
		
		if("noui".equals(uiMode)){
			return FunctionQuietRunner.runFunction(function, actionContext);
		}else if("dialog".equals(uiMode)){
			FunctionRequest request = new FunctionRequest(function, null, false, actionContext);
			FunctionRequestUI ui = new DialogUI();			
			FunctionRequestUIFactory.registUI(request, ui);
			ui.createUI(request);
		}else{
			FunctionRequest request = new FunctionRequest(function, null, false, actionContext);
			FunctionRequestUI ui = null;
			if(XWorkerUtils.getIde() == null){
				ui = new DialogUI();	
			}else{
				ui = new DesignUI();
			}
			FunctionRequestUIFactory.registUI(request, ui);
			ui.createUI(request);
		}
		
		return null;
	}
}