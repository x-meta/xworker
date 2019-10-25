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
package xworker.ui.swt;

import org.eclipse.swt.widgets.Widget;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.ui.AbstractUIHandler;
import xworker.ui.UIManager;
import xworker.ui.UIRequest;

/**
 * 抽象的SWTUIHandler，主要是增加了在SWT控件销毁时从UIManager取消注册的功能。
 * 
 * @author Administrator
 *
 */
public abstract class AbstractSWTUIHandler extends AbstractUIHandler{
	Widget widget;
	boolean regist;
	boolean sync = false;
	
	/**
	 * 抽象的SWTUIHandler构造器。
	 * 
	 * @param thing
	 * @param uiHandlerId
	 * @param widget
	 * @param regist
	 * @param actionContext
	 */
	public AbstractSWTUIHandler(Thing thing, String uiHandlerId, Widget widget, boolean regist, ActionContext actionContext) {
		super(thing, uiHandlerId, actionContext);
		
		if(regist){
			UIManager.registUIHandler(uiHandlerId, this);
			
			if(widget != null){
				widget.addDisposeListener(new UIHandlerDisposeListener(uiHandlerId));
			}
		}
		
		this.sync = thing.getBoolean("sync");
		this.widget = widget;
		this.regist = regist;
	}

	@Override
	public void requestUI(final UIRequest request, final ActionContext actionContext) {
		Runnable run = new Runnable(){
			public void run(){
				doRequeestUI(request, actionContext);
			}
		};
		if(sync){
			widget.getDisplay().syncExec(run);
		}else{
			widget.getDisplay().asyncExec(run);
		}
	}

	@Override
	public void finishUI(final UIRequest request, final ActionContext actionContext) {
		Runnable run = new Runnable(){
			public void run(){
				doFinishUI(request, actionContext);
			}
		};
		if(sync){
			widget.getDisplay().syncExec(run);
		}else{
			widget.getDisplay().asyncExec(run);
		}
	}

	protected abstract void doRequeestUI(UIRequest request, ActionContext actionContext);
	
	protected abstract void doFinishUI(UIRequest request, ActionContext actionContext);

	public Widget getWidget() {
		return widget;
	}
}