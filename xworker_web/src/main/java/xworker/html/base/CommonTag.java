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
package xworker.html.base;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

import xworker.html.HtmlConstants;
import xworker.html.jqueryeasyui.EasyUIUtils;

public class CommonTag {
	/**
	 * 转换成HTML。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static String toHtml(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String htmlTag = (String) self.doAction("getTagName", actionContext);
		if(htmlTag != null && !"".equals(htmlTag)){
			String html = "<" + htmlTag;
			html = EasyUIUtils.addAttribute(html, "id", self.getString("id"));
			html = EasyUIUtils.addAttribute(html, "name", self.getString("name"));
			html = EasyUIUtils.addAttribute(html, "style", self.getString("style"));
			html = EasyUIUtils.addAttribute(html, "class", self.getString("class"));
			String otherAttributes = self.getStringBlankAsNull("otherAttributes");
			if(otherAttributes != null){
				html = html + " " + otherAttributes;
			}
			html = html + ">";
			//子节点
			for(Thing child : self.getChilds()){
				String childHtml = (String) child.doAction(HtmlConstants.ACTION_TO_HTML, actionContext);
				if(childHtml != null){
					html = html + childHtml;
				}
			}
			
			return html + "</" + htmlTag + ">";
		}else{
			throw new ActionException("cat not get html tag name, thing=" + self.getMetadata().getPath());
		}
	}
	
	/**
	 * 获取标签名称。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static String getTagName(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		return self.getThingName().toLowerCase();
	}
	
	public static String getHtmlTagName(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		return self.getString("tagName");
	}
}