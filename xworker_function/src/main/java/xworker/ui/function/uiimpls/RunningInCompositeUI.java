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
package xworker.ui.function.uiimpls;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.ui.UIRequest;
import xworker.ui.function.FunctionRequest;
import xworker.ui.function.FunctionRequestUI;
import xworker.ui.function.FunctionRequestUIFactory;
import xworker.ui.swt.widgets.CompositeUIHandler;

/**
 * 交互函数执行在某个SWT Composite下的UI。
 * 
 * @author Administrator
 *
 */
public class RunningInCompositeUI extends FunctionRequestUI{
	FunctionRequest root;
	Composite compoiste;
	
	public RunningInCompositeUI(FunctionRequest functionRequest, CompositeUIHandler compositeUIHandler, ActionContext actionContext){
		root = functionRequest.getRoot();
		
		Thing compositeThing = World.getInstance().getThing("xworker.ui.function.FunctionRequestRunningShell/@dialogMainComposite");
		UIRequest uiRequest = new UIRequest(compositeThing, actionContext);
		uiRequest.setRequestMessage(this);
		
		compositeUIHandler.requestUI(uiRequest, actionContext);
		
		FunctionRequestUIFactory.registUI(root, this);
	}
	
	public void setComposite(Composite composite){
		this.compoiste = composite;
		this.compoiste.addDisposeListener(new DisposeListener(){
			@Override
			public void widgetDisposed(DisposeEvent event) {
				FunctionRequestUIFactory.removeUI(root);
			}
		});
	}
	
	@Override
	public void createUI(FunctionRequest functionRequest) {
	}

	@Override
	public void forceActive() {
	}

	@Override
	public void updateRequestUI(FunctionRequest functionRequest) {
	}

	@Override
	public void close() {	
	}


}