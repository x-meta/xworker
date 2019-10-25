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

import org.eclipse.swt.browser.Browser;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.ui.UIRequest;
import xworker.util.GlobalConfig;

/**
 * 绑定到一个Browser上，当一个事物请求UI时显示这个事物的描述页面。
 * 
 * @author Administrator
 *
 */
public class BrowserThingDescUIhandler extends AbstractSWTUIHandler{
	Browser browser;
	
	public BrowserThingDescUIhandler(Thing thing, String uiHandlerId, Browser browser, boolean regist, ActionContext actionContext){
		super(thing, uiHandlerId, browser, regist, actionContext);		
		
		this.browser = browser;
	}
	
	public static void create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		String uiHandlerId = (String) self.doAction("getUIHandlerId", actionContext);
		Browser parent = (Browser) actionContext.get("parent");
		
		boolean regist = self.getBoolean("registToUIManager");
		BrowserThingDescUIhandler uiHandler = new BrowserThingDescUIhandler(self, uiHandlerId, parent, regist, actionContext);
		
		actionContext.getScope(0).put(self.getMetadata().getName(), uiHandler);
	}

	@Override
	protected void doRequeestUI(UIRequest request,
			ActionContext actionContext) {
		String thingDescUrl = GlobalConfig.getThingDescUrl(request.getThing().getMetadata().getPath());
		if(!browser.isDisposed()){
			browser.setUrl(thingDescUrl);
		}
		this.requestCallback(request, null);
	}

	@Override
	protected void doFinishUI(UIRequest request, ActionContext actionContext) {
		this.finishCallback(request, null);
	}
}