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
package xworker.ui.swt.design;

import org.eclipse.swt.widgets.Control;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.design.Designer;
import xworker.ui.UIRequest;
import xworker.ui.swt.AbstractSWTUIHandler;

public class MarkUIHandler extends AbstractSWTUIHandler{
	public MarkUIHandler(Thing thing, String uiHandlerId, Control control, boolean regist, ActionContext actionContext){
		super(thing, uiHandlerId, control, regist, actionContext);
	}

	@Override
	protected void doRequeestUI(UIRequest request, ActionContext actionContext) {
		//标记控件
		Designer.markControl((Control) this.getWidget());
		
		this.requestCallback(request, null);
	}

	@Override
	protected void doFinishUI(UIRequest request, ActionContext actionContext) {
		//取消控件的标记
		Designer.unmarkControl((Control) this.getWidget());
		
		this.finishCallback(request, null);
	}

	public static void create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		String uiHandlerId = (String) self.doAction("getUIHandlerId", actionContext);
		Control parent = (Control) actionContext.get("parent");
		
		boolean regist = self.getBoolean("registToUIManager");
		MarkUIHandler uiHandler = new MarkUIHandler(self, uiHandlerId, parent, regist, actionContext);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), uiHandler);
	}
}