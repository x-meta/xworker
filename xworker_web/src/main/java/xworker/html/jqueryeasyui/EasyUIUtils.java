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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.html.HtmlConstants;

public class EasyUIUtils {
	private static final String EASYUISCRIPTTHING = "__jquery_easy_ui_script_thing__";
	
	public static String[] noAttributeThing = new String[]{
		"xworker.lang.MetaThing",
		"xworker.html.jqueryesayui.EasyUIThing",
		"xworker.html.jqueryesayui.EasyUIFunction",
		"xworker.html.jqueryesayui.EasyUIArray"
	};
	
	public static void addToBottmInitThing(Thing thing, ActionContext actionContext){
		Thing javaScriptThing = (Thing) actionContext.get(HtmlConstants.HTML_BOTTOM_JAVASCRIPT_THING);
		if(javaScriptThing != null){
			List<Thing> bottomThings = javaScriptThing.getChilds();//(List<Thing>) actionContext.get(HtmlConstants.HTML_BOTTOM_THINGS);
			if(bottomThings != null){
				Thing easyInitThing = null;
				for(Thing bthing : bottomThings){
					if(bthing.getDescriptor().getMetadata().getPath().equals("xworker.html.jqueryesayui.EasyUIInitScript")){
						easyInitThing = bthing;
						break;
					}
				}
				
				if(easyInitThing == null){
					easyInitThing = new Thing("xworker.html.jqueryesayui.EasyUIInitScript");
					bottomThings.add(easyInitThing);
				}
				
				//同一个事物只能添加一次
				boolean have = false;
				for(Thing child : easyInitThing.getChilds()){
					if(child == thing || child.getMetadata().getPath().equals(thing.getMetadata().getPath())){
						have = true;
					}
					break;
				}
				
				if(!have){
					easyInitThing.addChild(thing, false);
				}
			}		
		}
	}
	
	/**
	 * 用于保存EasyUI事物，比如一个EasyUI事物是绑定到一个已有的div上的，那么需要在最后用javascript绑定。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Thing getEasyUIScriptThing(ActionContext actionContext){
		Thing scriptThing = (Thing) actionContext.get(EASYUISCRIPTTHING);
		return scriptThing;
	}
	
	public static String addAttribute(String str, String name, String value){
		if(value != null && !"".equals(value)){
			return str + " " + name + "=\"" + value + "\"";
		}else{
			return str;
		}
	}
	
	/**
	 * 返回EasyU的javascript字符串，本方法返回的不是完整的，是{}之内的部分，完整的需要使用getJavaScript方法返回。
	 * 
	 * @param thing
	 * @return
	 */
	public static String getScriptAttributes(Thing thing, ActionContext actionContext){
		return getScriptAttributes(thing, actionContext, null);
	}
	
	/**
	 * 返回EasyU的javascript字符串，本方法返回的不是完整的，是{}之内的部分，完整的需要使用getJavaScript方法返回。
	 * 
	 * @param thing
	 * @return
	 */
	public static String getScriptAttributes(Thing thing, ActionContext actionContext, String alowChilds[]){		
		List<Thing> attributes = thing.getAllAttributesDescriptors();
		String attr = null;
		
		//属性
		//String[] excludeNames = new String[]{"name", "label", "id", "descriptors", "extends"};
		//避免重复的名字
		Map<String,String> context = new HashMap<String, String>();
		for(Thing attribute : attributes){
			String path = attribute.getMetadata().getPath();
			boolean notGen = false;
			for(String exPath : noAttributeThing){
				if(path.startsWith(exPath)){
					notGen = true;
					break;
				}
			}
			if(notGen){
				continue;
			}
			
			
			String name = attribute.getString("name");
			if(context.get(name) != null){
				continue;
			}
			
			String value = thing.getString(name);
			if(value != null && !"".equals(value) && !value.equals(attribute.getString("default"))){
				if(attr != null){
					attr = attr + ",";
				}else{
					attr = "";
				}
								
				context.put(name, name);
				attr = attr + name + ":" + value;
			}
		}
		
		//子事物
		for(Thing child : thing.getChilds()){
			String name = child.getString("name");
			if(context.get(name) != null){
				continue;
			}
			if(alowChilds != null){
				boolean have = false;
				String childName = child.getThingName();
				for(String ac : alowChilds){
					if(ac.equals(childName)){
						have = true;
						break;
					}
				}
				if(!have){
					continue;
				}
			}
			
			String script = (String) child.doAction("toJavaScriptCode", actionContext);
			if(script != null){
				//script = HtmlUtil.getIdentString(script, "    ");
				if(attr !=null){
					attr = attr + ",\n";				
					attr = attr + child.getString("name") + ":" + script;
				}else{
					attr = child.getString("name") + ":" + script;
				}
				
				context.put(name, name);
			}
		}
		
		return attr;
	}
}