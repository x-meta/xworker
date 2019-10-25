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

import xworker.ui.UIRequest;
import xworker.ui.swt.AbstractSWTUIHandler;

public class ThingFormUIHandler extends AbstractSWTUIHandler{
	/** 表单事物 */
	Thing formThing;
	
	/** 当前正在处理的请求 */
	UIRequest currentRequest;
	
	public ThingFormUIHandler(Thing thing, String uiHandlerId, Thing formThing, boolean regist, ActionContext actionContext){
		super(thing, uiHandlerId, (Composite) formThing.getData("parent"), regist, actionContext);
		
		this.formThing = formThing;
	}
	
	public UIRequest getCurrentRequest(){
		return currentRequest;
	}

	public static void create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		String uiHandlerId = (String) self.doAction("getUIHandlerId", actionContext);
		Composite parent = (Composite) actionContext.get("parent");
		Thing formThing = (Thing) parent.getData("formThing");
		
		boolean regist = self.getBoolean("registToUIManager");
		ThingFormUIHandler uiHandler = new ThingFormUIHandler(self, uiHandlerId, formThing, regist, actionContext);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), uiHandler);
	}

	@Override
	protected void doRequeestUI(UIRequest request, ActionContext actionContext) {
		//线程同步，swt不允许其他线程访问控件
		ActionContext ac = (ActionContext) formThing.getData("actionContext");
		formThing.doAction("setDescriptor", ac, UtilMap.toMap(new Object[]{"descriptor", request.getThing(), "request", request}));
		currentRequest = request;
		
		this.requestCallback(request, UtilMap.toMap(new Object[]{"formThing", formThing}));
	}

	@Override
	protected void doFinishUI(UIRequest request, ActionContext actionContext) {
		this.finishCallback(request, null);
	}
}