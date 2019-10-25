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
import org.xmeta.Thing;

import xworker.html.HtmlUtil;
import xworker.html.jqueryeasyui.EasyUIUtils;

public class MenuItem {
	/**
	 * MenuItem的生成HTML的方法。
	 * 
	 * @param actionContext
	 * @return
	 * @throws Throwable 
	 */
	public static String toHtml(ActionContext actionContext) throws Throwable{
		Thing self = (Thing) actionContext.get("self");
		
		if(self.getBoolean("split")){
			//分隔符
			return "<div class=\"menu-sep\"></div>";
		}
		
		String html = "";
		String ident = (String) actionContext.get("ident");
		if(ident == null){
			ident = "";
		}
		
		//需要生成div标签
		html = "<div";
		
		//id
		html = EasyUIUtils.addAttribute(html, "id", self.getString("id"));

		//name
		html = EasyUIUtils.addAttribute(html, "name", self.getString("name"));
		
		//style
		if(self.getChilds().size() == 0){
			html = EasyUIUtils.addAttribute(html, "style", self.getString("style"));
		}
		
		Thing onclickThing = self.getThing("onclick@0");
		if(onclickThing != null){
			HtmlUtil.addBottomJavaScriptThing(onclickThing, actionContext);
			html = html + " onclick=\"javascript:" + onclickThing.getString("name") + "()\"";
		}
		
		//data-options
		String options = null;
		String iconCls = self.getStringBlankAsNull("iconCls");
		if(iconCls != null){
			options = "iconCls:" + iconCls;
		}
		String href = self.getStringBlankAsNull("href");
		if(href != null){
			if(options != null){
				options = options + ",href:" + href; 
			}else{
				options = "href:" + href;
			}
		}
		String disabled = self.getStringBlankAsNull("disabled");
		if(href != null){
			if(options != null){
				options = options + ",disabled:" + disabled; 
			}else{
				options = "disabled:" + disabled;
			}
		}
		
		
		//data-options
		html = EasyUIUtils.addAttribute(html, "data-options", HtmlUtil.getHtmlString(options));
		
		String otherAttributes = self.getStringBlankAsNull("otherAttributes");
		if(otherAttributes != null){
			html = html + " " + otherAttributes;
		}
		
		html = html + ">";
		
		//是否拥有子节点
		boolean hasChild = self.getChilds().size() != self.getChilds("onclick").size();
		if(!hasChild){
			html = html + self.getString("text") + "</div>";
		}else{
			html = html + "\n" + ident + "<span>" + self.getString("text")  + "</span>";
			html = html + "\n" + ident + "<div";
			html = EasyUIUtils.addAttribute(html, "style", self.getString("style"));
			html = html + ">";
			//生成子节点
			for(Thing child : self.getChilds()){
				String childHtml = (String) child.doAction("toHtml", actionContext);
				if(childHtml != null){
					html = html + "\n" + ident + "    " + HtmlUtil.getIdentString(childHtml, ident);
				}
			}
			html = html + "\n" + ident + "</div>";
			html = html + "\n</div>";
		}
		
		return html;
	}
}