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
package xworker.ui.function.xworker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import xworker.ui.UIHandler;
import xworker.ui.UIRequest;
import xworker.ui.function.FunctionCallback;
import xworker.ui.function.FunctionManager;
import xworker.ui.function.FunctionRequest;

public class ActionContextViewer {
	
	public static void buttonHandler(ActionContext actionContext){
		Event event = (Event) actionContext.get("event");
		UIRequest uiRequest = (UIRequest) actionContext.get("request");
		if(uiRequest != null){
			FunctionRequest functionRequest = (FunctionRequest) uiRequest.getRequestMessage();
			if(event.widget == actionContext.get("cacnelButton")){
				if(functionRequest.getCallback() == null){
					FunctionManager.sendRequest(functionRequest, actionContext);
					return;
				}else{
					functionRequest.getCallback().cancel(actionContext);
					return;
				}
			}

			Thing objectViewer = (Thing) actionContext.get("objectViewer");
			ActionContext ac = (ActionContext) objectViewer.getData("actionContext");
			Tree tree = (Tree) ac.get("dataTree");
			if(tree.getSelection().length > 0){
				TreeItem item = tree.getSelection()[0];
				Object value = null;
				if(event.widget == actionContext.get("returnNameButton")){
					value = (String) item.getData("name");
				}else if(event.widget == actionContext.get("returnValueButton")){
					value = item.getData();
				}
				
				if(functionRequest.getCallback() == null){
					FunctionManager.finishRequest(functionRequest, value);
					return;
				}else{
					functionRequest.getCallback().ok(value, actionContext);
					return;
				}
			}
		}
	}
	
	public static void init(ActionContext actionContext){
		UIHandler browserHandler = (UIHandler) actionContext.get("browserHandler");		
		UIRequest uiRequest = (UIRequest) actionContext.get("request");
		FunctionRequest unRequest = (FunctionRequest) uiRequest.getRequestMessage();
		//变量名列表
		List<String> keys = new ArrayList<String>();
		for(String key : unRequest.getActionContext().keySet()){
			if(key != null){
				keys.add(key);
			}
		}
		Collections.sort(keys);
		
		//变量列表，ObjectViewer可以使用的
		List<Map<String, Object>> objects = new ArrayList<Map<String, Object>>();
		for(String key : keys){
			Map<String, Object> value = new HashMap<String, Object>();
			value.put("name", key);
			value.put("object", unRequest.getActionContext().get(key));
			objects.add(value);
		}
		
		//显示变量
		Thing objectViewer = (Thing) actionContext.get("objectViewer");
		objectViewer.doAction("setObjects", actionContext, UtilMap.toMap(new Object[]{"objects", objects}));
		
		//显示文档		
		if(uiRequest != null && browserHandler != null){
			FunctionRequest functionRequest = (FunctionRequest) uiRequest.getRequestMessage();
			if(functionRequest != null){
				FunctionCallback callback = functionRequest.getCallback();
				if(callback != null){
					browserHandler.requestUI(new UIRequest(callback.getParam().getDescriptor(), actionContext),  actionContext);
				}else{
					browserHandler.requestUI(new UIRequest(functionRequest.getDescriptor(), actionContext),  actionContext);
				}
			}
		}
	}
}