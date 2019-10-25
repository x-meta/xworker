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

import xworker.html.HtmlUtil;

public class EasyUIThing {
	/**
	 * 转成HTML。
	 * 
	 * @param actionContext
	 * @return
	 * @throws Throwable 
	 */
	public static String toHtml(ActionContext actionContext) throws Throwable{
		Thing self = (Thing) actionContext.get("self");
		
		String html = "";
		String ident = (String) actionContext.get("ident");
		if(ident == null){
			ident = "";
		}
		
		//需要生成div标签
		if(self.getBoolean("generateDiv")){
			String htmlTag = (String) self.doAction("getHtmlTag", actionContext);
			html = "<" + htmlTag + " ";
			
			//id
			html = EasyUIUtils.addAttribute(html, "id", self.getString("id"));

			//name
			html = EasyUIUtils.addAttribute(html, "name", self.getString("name"));
			
			//style
			html = EasyUIUtils.addAttribute(html, "style", self.getString("style"));
			
			//class
			//html = EasyUIUtils.addAttribute(html, "class", (String) self.doAction("getClass", actionContext));
			
			//data-options
			//html = EasyUIUtils.addAttribute(html, "data-options", HtmlUtil.getHtmlString(EasyUIUtils.getScriptAttributes(self, actionContext)));
			
			String otherAttributes = self.getStringBlankAsNull("otherAttributes");
			if(otherAttributes != null){
				html = html + " " + otherAttributes;
			}
			
			html = html + ">";
			
			//生成子节点
			for(Thing child : self.getChilds()){
				String childHtml = (String) child.doAction("toHtml", actionContext);
				if(childHtml != null){
					html = html + "\n" + ident + HtmlUtil.getIdentString(childHtml, ident);
				}
			}
			
			html = html + "\n</" + htmlTag + ">";
		}else{
			//子节点应该还是要继续生成的
			for(Thing child : self.getChilds()){
				String childHtml = (String) child.doAction("toHtml", actionContext);
				if(childHtml != null){
					html = html + "\n" + ident + HtmlUtil.getIdentString(childHtml, ident);
				}
			}
		}
		
		EasyUIUtils.addToBottmInitThing(self, actionContext);
		
		return html;
	}
	
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
			script = EasyUIUtils.getScriptAttributes(self, actionContext);			
		}finally{
			actionContext.pop();
		}
		
		if(script == null){
			script = "";
		}
		
		script = "{" + HtmlUtil.getIdentString(script, "    ") + "}";
		if(isInit == null || isInit == true){
			return "$('#" + self.getString("id") + "')." + self.getThingName().toLowerCase() + "(" + script + ");";
		}else{
			return script;
		}
	}
	
	/**
	 * 返回EasyUI的class名称。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static String getClass(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		return "easyui-" + self.getThingName().toLowerCase();
	}
	
	/**
	 * 返回HTML标签。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static String getHtmlTag(ActionContext actionContext){
		return "div";
	}
}