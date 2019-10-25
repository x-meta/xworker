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
package xworker.ui.swt.xworker;

import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import xworker.swt.ActionContainer;
import xworker.ui.UIRequest;
import xworker.ui.swt.AbstractSWTUIHandler;

public class ThingEditorUIHandler extends AbstractSWTUIHandler{
	/** 编辑器的变量上下文 */
	ActionContext editorContext;
	
	/** 当前正在处理的请求 */
	UIRequest currentRequest;
	
	public ThingEditorUIHandler(Thing thing, String uiHandlerId, ActionContext editorContext, boolean regist, ActionContext actionContext){
		super(thing, uiHandlerId, (Composite) editorContext.get("parent"), regist, actionContext);
		
		this.editorContext = editorContext;
	}
	
	public UIRequest getCurrentRequest(){
		return currentRequest;
	}

	public static void create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		String uiHandlerId = (String) self.doAction("getUIHandlerId", actionContext);
		Composite parent = (Composite) actionContext.get("parent");
		ActionContext editorContext = (ActionContext) parent.getData("actionContext");
		boolean regist = self.getBoolean("registToUIManager");
		ThingEditorUIHandler uiHandler = new ThingEditorUIHandler(self, uiHandlerId, editorContext, regist, actionContext);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), uiHandler);
	}

	@Override
	protected void doRequeestUI(UIRequest request, ActionContext actionContext) {
		//设置编辑编辑新的事物
		ActionContainer editorActions = (ActionContainer) editorContext.get("editorActions");		
		editorActions.doAction("setThing", editorContext, UtilMap.toMap(new Object[]{"thing", request.getRequestMessage()}));
		
		currentRequest = request;
		
		this.requestCallback(request, UtilMap.toMap(new Object[]{"editorContext", editorContext}));
	}

	@Override
	protected void doFinishUI(UIRequest request, ActionContext actionContext) {
		this.finishCallback(request, null);
	}
}