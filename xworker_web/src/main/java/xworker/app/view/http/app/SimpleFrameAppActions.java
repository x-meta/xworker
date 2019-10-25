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
package xworker.app.view.http.app;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.http.ResultView;
import xworker.util.UtilTemplate;

public class SimpleFrameAppActions {
	public static void httpDo(HttpServletRequest request, HttpServletResponse response, ActionContext actionContext) throws Throwable{
		Thing self = (Thing) actionContext.get("appThing");
		
		//是否执行初始化的方法
		if(self.getBoolean("init")){
			self.doAction("init", actionContext);
		}
		
		String ac = request.getParameter("ac");
		response.setContentType("text/html; charset=utf-8");
		if(ac == null || "".equals(ac)){			
			Thing headThing = World.getInstance().getThing("xworker.app.view.http.app.SimpleFrameApp/@code");
			UtilTemplate.processThingAttribute(headThing, "code", actionContext, response.getWriter(), "utf-8");
		}else if("head".equals(ac)){
			UtilTemplate.processThingAttribute(self, "headCode", actionContext, response.getWriter(), "utf-8");

			UtilTemplate.processThingAttribute(self, "headHtml", actionContext, response.getWriter(), "utf-8");
		}else if("menu".equals(ac)){
			Object menu = self.doAction("getMenu", actionContext);
			if(menu == null){
				response.getWriter().println("menu not setted!");
			}else{
				actionContext.getScope(0).put("menus", menu);
				Thing formObject = World.getInstance().getThing("xworker.app.view.http.app.SimpleFrameAppMenuView");
				ResultView.processViewThing(formObject, actionContext);
				
			}
		}		
	}

	public static Thing getMenu(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		return self.getThing("menu@0");
	}
}