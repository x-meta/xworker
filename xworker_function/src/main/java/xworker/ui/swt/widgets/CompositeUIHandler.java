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
package xworker.ui.swt.widgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.ui.UIRequest;
import xworker.ui.function.FunctionParameter;
import xworker.ui.function.FunctionRequest;
import xworker.ui.swt.AbstractSWTUIHandler;

/**
 * 每次访问都会清空Composite，然后调用请求事物的create方法重新制作面板。
 * 
 * @author Administrator
 *
 */
public class CompositeUIHandler extends AbstractSWTUIHandler{
	/** Composite */
	Composite composite;
	
	ActionContext actionContext;
	
	/** 当前正在处理的请求 */
	UIRequest currentRequest;
	
	public CompositeUIHandler(Thing thing, String uiHandlerId, Composite composite, boolean regist, ActionContext actionContext){
		super(thing, uiHandlerId, composite, regist, actionContext);
		
		this.composite = composite;
	}
	
	public UIRequest getCurrentRequest(){
		return currentRequest;
	}

	public static void create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		String uiHandlerId = (String) self.doAction("getUIHandlerId", actionContext);
		Composite parent = (Composite) actionContext.get("parent");
		
		boolean regist = self.getBoolean("registToUIManager");
		CompositeUIHandler uiHandler = new CompositeUIHandler(self, uiHandlerId, parent, regist, actionContext);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), uiHandler);
		
		uiHandler.actionContext = actionContext;
	}

	@Override
	protected void doRequeestUI(UIRequest request, ActionContext actionContext) {
		//首先清空已有的组件
		for(Control control : composite.getChildren()){
			control.dispose();
		}
		
		//创建面板，使用新的变量上下文
		currentRequest = request;		
		ActionContext ac = new ActionContext(this.actionContext);
		ac.peek().put("parent", composite);
		ac.peek().put("request", request);
		ac.put("parentContext", this.actionContext);
		//参数
		if(request.getRequestMessage() instanceof FunctionRequest) {
			FunctionRequest fr = (FunctionRequest) request.getRequestMessage();
			for(FunctionParameter param : fr.getParameters()) {
				ac.put(param.getName(), param.getValue());
			}
		}
		request.getThing().doAction("create", ac);
		
		composite.layout();
		
		//回调
		this.requestCallback(request, null);
	}

	@Override
	protected void doFinishUI(UIRequest request, ActionContext actionContext) {
		this.finishCallback(request, null);
	}

}