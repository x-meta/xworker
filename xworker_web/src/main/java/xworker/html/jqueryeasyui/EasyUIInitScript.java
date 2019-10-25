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
package xworker.html.jqueryeasyui;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import xworker.html.HtmlConstants;
import xworker.html.HtmlUtil;

public class EasyUIInitScript {
	/**
	 * 转化成 HTML。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static String toHtml(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		String html = "<script language=\"javascript\">\n";
		html = html + "$(function(){";
		Bindings bindings = actionContext.push();
		bindings.put("esay-ui-init", true);
		try{
			for(int i=0; i<self.getChilds().size(); i++){
				Thing child = self.getChilds().get(i);
				String script = (String) child.doAction(HtmlConstants.ACTION_TO_JAVA_SCRIPT, actionContext);
				if(script != null){
					html = html + "\n    " + HtmlUtil.getIdentString(script, "    ");
				}
			}
		}finally{
			actionContext.pop();
		}
		html = html + "\n});\n</script>";
		return html;
	}
	
	public static String toJavaScriptCode(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String html = "$(function(){";
		Bindings bindings = actionContext.push();
		bindings.put("esay-ui-init", true);
		try{
			for(int i=0; i<self.getChilds().size(); i++){
				Thing child = self.getChilds().get(i);
				String script = (String) child.doAction(HtmlConstants.ACTION_TO_JAVA_SCRIPT, actionContext);
				if(script != null){
					html = html + "\n    " + HtmlUtil.getIdentString(script, "    ");
				}
			}
		}finally{
			actionContext.pop();
		}
		html = html + "\n});";
		return html;
	}
}