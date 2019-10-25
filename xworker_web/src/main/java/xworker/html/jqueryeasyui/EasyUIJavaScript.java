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

import xworker.html.HtmlConstants;
import xworker.html.HtmlUtil;

/**
 * 添加到$(function(){});之中的代码。
 * 
 * @author Administrator
 *
 */
public class EasyUIJavaScript {
	public static Object toHtml(ActionContext actionContext){
		return null;
		/*
		Thing self = (Thing) actionContext.get("self");
		//第一次toJavaScriptCode是重构一个自己，并当放到最后
		Thing thing = new Thing("xworker.html.jqueryesayui.EasyUIJavaScript");
		thing.getAttributes().putAll(self.getAttributes());
		thing.getMetadata().setPath(self.getMetadata().getPath());
		for(Thing child : self.getChilds()){
			thing.addChild(child, false);
		}
		thing.put("_is_real_to_java_script_code", "true");
		EasyUIUtils.addToBottmInitThing(thing, actionContext);
		return null;
		*/
	}
	
	public static String toJavaScriptCode(ActionContext actionContext){		
		Thing self = (Thing) actionContext.get("self");
		
		if(!self.getBoolean("_is_real_to_java_script_code")){
			//第一次toJavaScriptCode是重构一个自己，并当放到最后
			Thing thing = new Thing("xworker.html.jqueryesayui.EasyUIJavaScript");
			thing.getAttributes().putAll(self.getAttributes());
			thing.getMetadata().setPath(self.getMetadata().getPath());
			for(Thing child : self.getChilds()){
				thing.addChild(child, false);
			}
			thing.put("_is_real_to_java_script_code", "true");
			EasyUIUtils.addToBottmInitThing(thing, actionContext);
			return null;
		}
		
		String script = null;
		if(self.getBoolean("useChildsCode")){
			for(Thing child : self.getChilds()){
				String s = (String) child.doAction(HtmlConstants.ACTION_TO_JAVA_SCRIPT, actionContext);
				if(s != null && !"".equals(s.trim())){
					if(script != null){
						script = script + "\n\n" + s;
					}else{
						script = s;
					}
				}
			}
		}else{
			script = self.getString("code");
		}
				
		if(script != null){
			return  HtmlUtil.getIdentString(script, ""); 
		}else{
			return null;
		}
	}
}