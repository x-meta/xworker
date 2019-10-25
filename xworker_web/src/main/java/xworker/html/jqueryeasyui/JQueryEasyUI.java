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
import org.xmeta.Thing;

import xworker.html.HtmlUtil;

public class JQueryEasyUI {
	public static void toHtml(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");

		//jquery的类库
		if(self.getBoolean("includeJQuery")){
			HtmlUtil.addHeader("jquery", "\n<script type=\"text/javascript\" src=\"${request.contextPath}/js/jquery-easyui/" + self.getString("version") + "/jquery.min.js\"></script>", actionContext);
		}
		
		//easyui的类库
		HtmlUtil.addHeader("jquery-easyui", "\n<script type=\"text/javascript\" src=\"${request.contextPath}/js/jquery-easyui/" + self.getString("version") + "/jquery.easyui.min.js\"></script>", actionContext);

		//xworker easyui相关类库
		HtmlUtil.addHeader("jquery-easyui-xworker", "\n<script type=\"text/javascript\" src=\"${request.contextPath}/js/xworker/xworkerJQueryEasyUI.js\"></script>", actionContext);
		
		//css
		HtmlUtil.addHeader("jquery-easyui-themes-default", "\n<link rel=\"stylesheet\" type=\"text/css\" href=\"${request.contextPath}/js/jquery-easyui/" + self.getString("version") + "/themes/default/easyui.css\"></link>", actionContext);
		HtmlUtil.addHeader("jquery-easyui-icons", "\n<link rel=\"stylesheet\" type=\"text/css\" href=\"${request.contextPath}/js/jquery-easyui/" + self.getString("version") + "/themes/icon.css\"></link>", actionContext);
		
		
		//easyloader的类库
		if(self.getBoolean("includeEasyLoader")){
			HtmlUtil.addHeader("jquery-easyui-easyloader", "\n<script type=\"text/javascript\" src=\"${request.contextPath}/js/jquery-easyui/" + self.getString("version") + "/easyloader.js\"></script>", actionContext);
		}
	}
}