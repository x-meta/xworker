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
import xworker.ui.function.uiimpls.DesignUI;
import xworker.ui.function.uiimpls.RunningUI;

public class FunctionRequestUIFactory {
	private static Logger logger = LoggerFactory.getLogger(FunctionRequestUIFactory.class);	
	private static Map<FunctionRequest, FunctionRequestUI> requestUIs = new HashMap<FunctionRequest, FunctionRequestUI>();
	
	/** 对话面板的UI标识 */
	public static final String UI_DIALOG_COMPOSITE = "xworker_session_swt_dialogComposite";
	/** 对话表单的UI标识 */
	public static final String UI_DIALOG_FORM = "xworker_session_swt_dialogForm";
	
	public static void requestUI(FunctionRequest functionRequest, String uiHandlerId, UIRequest uiRequest, ActionContext actionContext){
		FunctionRequestUI ui = getFunctionRequestUI(functionRequest);
		if(ui == null){
			logger.info("FunctionRequestUI is null, function=" + functionRequest);
			return;
		}
		
		ui.requestUI(uiHandlerId, uiRequest, actionContext);
	}
	
	public static void requestUI(FunctionRequest functionRequest, String uiHandlerId, Thing requestThing, ActionContext actionContext){
		FunctionRequestUI ui = getFunctionRequestUI(functionRequest);
		if(ui == null){
			logger.info("FunctionRequestUI is null, function=" + functionRequest);
			return;
		}
		
		ui.requestUI(functionRequest, uiHandlerId, requestThing, actionContext);
	}
	
	public static void updateRequestUI(FunctionRequest functionRequest){
		FunctionRequestUI ui = getFunctionRequestUI(functionRequest);
		if(ui == null){
			logger.info("FunctionRequestUI is null, function=" + functionRequest);
			return;
		}
		
		ui.updateRequestUI(functionRequest);
	}
	
	public static UIHandler getUIHandler(FunctionRequest functionRequest, String uiHandlerId){
		FunctionRequestUI ui = getFunctionRequestUI(functionRequest);
		if(ui == null){
			logger.info("FunctionRequestUI is null, function=" + functionRequest);
			return null;
		}
		
		return ui.getUIHandler(uiHandlerId);
	}
	
	public static FunctionRequestUI getFunctionRequestUI(FunctionRequest functionRequest){
		return getFunctionRequestUI(functionRequest, true);
	}
	
	/**
	 * 返回函数请求的UI。
	 * 
	 * @param functionRequest
	 * @return
	 */
	public static FunctionRequestUI getFunctionRequestUI(FunctionRequest functionRequest, boolean design){
		FunctionRequest root = functionRequest.getRoot().getUiKey();
		FunctionRequestUI ui = requestUIs.get(root);
		if(ui == null){
			ui = createUI(root, design);
			requestUIs.put(root, ui);
		}
		
		return ui;
	}
	
	
	
	/**
	 * 创建函数请求的UI。
	 * 
	 * @param root
	 * @return
	 */
	private static FunctionRequestUI createUI(FunctionRequest root, boolean design){
		FunctionRequestUI ui = null;
		if(design){
			ui = new DesignUI();
		}else{
			ui = new RunningUI();
		}
		
		ui.createUI(root);
		
		return ui;
	}
	
	/**
	 * 关闭UI。
	 * 
	 * @param root
	 */
	public static void closeUI(FunctionRequest root){
		FunctionRequestUI ui = getFunctionRequestUI(root);
		if(ui != null){
			ui.close();
		}
		
		removeUI(root);
	}
	
	public static void removeUI(FunctionRequest root){
		requestUIs.remove(root);
	}
	
	public static void registUI(FunctionRequest root, FunctionRequestUI ui){
		requestUIs.put(root, ui);
	}
}