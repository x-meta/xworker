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
package xworker.html.jqueryeasyui.menubutton;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import xworker.html.HtmlUtil;
import xworker.html.jqueryeasyui.EasyUIUtils;

public class Menu {
	
	/**
	 * EasyUI的事物转化成JavaScript。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static String toJavaScript(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Boolean isInit = (Boolean) actionContext.get("esay-ui-init");
		String script = null;
		try{
			Bindings bindings = actionContext.push();
			bindings.put("esay-ui-init", false);
			script = EasyUIUtils.getScriptAttributes(self, actionContext, new String[]{"onShow", "onHide", "onClick"});			
		}finally{
			actionContext.pop();
		}
		
		if(script == null){
			script = "";
		}
		
		script = "{" + HtmlUtil.getIdentString(script, "    ") + "\n}";
		if(isInit == null || isInit == true){
			return "$('#" + self.getString("id") + "')." + self.getThingName().toLowerCase() + "(" + script + ");";
		}else{
			return script;
		}
	}
	
}