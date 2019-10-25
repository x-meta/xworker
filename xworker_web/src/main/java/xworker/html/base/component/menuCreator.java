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
package xworker.html.base.component;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilMap;

import xworker.html.HtmlUtil;
import xworker.html.Menu;
import xworker.util.UtilTemplate;

public class menuCreator {
	public static Object toHtml(ActionContext actionContext) throws Throwable {
		String html = "<script src=\"${request.contextPath}/js/xtree.js\"></script>\n"
				+ "<link type=\"text/css\" rel=\"stylesheet\" href=\"${request.contextPath}/js/xtree.css\">\n"
				+ "<script type=\"text/javascript\" src=\"${request.contextPath}/js/jscookmenu/JSCookMenu.js\"></script>\n"
				+ "<link rel=\"stylesheet\" href=\"${request.contextPath}/js/jscookmenu/ThemeOffice/theme.css\" type=\"text/css\">\n"
				+ "<script language=\"JavaScript\" src=\"${request.contextPath}/js/jscookmenu/ThemeOffice/theme.js\"></script>";
		HtmlUtil.addHeader("menu", html, actionContext);

		Thing self = (Thing) actionContext.get("self");
		String bottom = UtilTemplate.process(
				UtilMap.toMap(new Object[] { "object", self }),
				"/xworker/html/base/templates/menuMacro.ftl", "freemarker",
				"utf-8");
		HtmlUtil.addBottom("menu", bottom, actionContext);

		Menu menu = new Menu(self);
		return UtilTemplate.process(
				UtilMap.toMap(new Object[] { "object", self, "menu", menu }),
				"xworker/html/base/templates/menu.ftl", "freemarker", "utf-8");
	}

}