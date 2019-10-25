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
package xworker.html;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import freemarker.template.TemplateException;
import xworker.html.module.ModuleRequire;
import xworker.http.ResultView;
import xworker.util.UtilTemplate;

public class HtmlActions {
	@SuppressWarnings("unchecked")
	public static void addHead(Thing self, String key , String code, ActionContext actionContext) throws IOException, TemplateException{
		boolean exists = false;
        List<Map<String, String>> heads = (List<Map<String, String>>) actionContext.get("heads");
        
        if(heads == null || key == null || "".equals(key) || code == null || "".equals(code)){
        	return;
        }
        
        for(Map<String, String> head : heads){
            if(key.equals(head.get("name"))){
                exists = true;
                break;
            }                    
        }
        
        if(!exists){
	        Map<String, String> head = new HashMap<String, String>(); 
	        head.put("name",  key);
	        head.put("value", UtilTemplate.processString(actionContext, code));
	        
	        if(self.getBoolean("first")){
	        	heads.add(0, head);
	        }else{
	        	heads.add(head);
	        }
        }
	}
	
	public static void requireModule(String module, String version, ActionContext actionContext) {
		if(module == null) {
			return;
		}
		
		List<ModuleRequire> requiredModules = actionContext.getObject("requiredModules");
		if(requiredModules != null) {
			boolean have = false;
			for(ModuleRequire mr : requiredModules) {
				have = mr.equals(module, version);
				if(have) {
					break;
				}
			}
			
			if(!have) {
				ModuleRequire mr = new ModuleRequire(module, version);
				requiredModules.add(mr);
			}
		}
	}
	
	//requireModule的动作实现
	public static void requireModule(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		String module = self.doAction("getModule", actionContext);
		String version = self.doAction("getVersion", actionContext);
		
		requireModule(module, version, actionContext);
	}
	
	public static void addHead(ActionContext actionContext) throws IOException, TemplateException{
		Thing self = (Thing) actionContext.get("self");
		String key = self.getString("key");
		String code = self.getString("code");
		
		addHead(self, key, code, actionContext);
	}
	
	@SuppressWarnings("unchecked")
	public static void addBottom(String key , String  code, ActionContext actionContext) throws IOException, TemplateException{
		boolean exists = false;
        List<Map<String, String>> bottons = (List<Map<String, String>>) actionContext.get("bottoms");
        
        if(bottons == null || key == null || "".equals(key) || code == null || "".equals(code)){
        	return;
        }
        
        for(Map<String, String> botton : bottons){
            if(key.equals(botton.get("name"))){
                exists = true;
                break;
            }                    
        }
        
        if(!exists){
	        Map<String, String> botton = new HashMap<String, String>(); 
	        botton.put("name",  key);
	        botton.put("value", UtilTemplate.processString(actionContext, code));
	        bottons.add(botton);
        }
	}
	
	public static void addBottom(ActionContext actionContext) throws IOException, TemplateException{
		Thing self = (Thing) actionContext.get("self");
		String key = self.getString("key");
		String code = self.getString("code");
		
		addBottom(key, code, actionContext);
	}
	
	public static void addScriptThing(String key, ActionContext actionContext){		
		Thing self = actionContext.getObject("self");		
		Thing script = (Thing) self.doAction("getScriptThing", actionContext);
		boolean first = self.getBoolean("first");
		addScriptThing(key, script, first, actionContext);		
	}
	
	public static void addScriptThing(String key, Thing script, boolean first, ActionContext actionContext) {
		Thing scripts = actionContext.getObject(key);
		if(scripts == null){
			return;
		}
		
		if(script != null){
			if(first){
				scripts.addChild(script, 0, false);
			}else{
				scripts.addChild(script, false);
			}
		}			
	}
	
	public static void addHeadScriptThing(ActionContext actionContext){
		addScriptThing(HtmlConstants.HTML_HEADS_JAVASCRIPT_THING, actionContext);
	}
	
	public static void addBottonScriptThing(ActionContext actionContext){
		addScriptThing(HtmlConstants.HTML_BOTTOM_JAVASCRIPT_THING, actionContext);
	}
	
	@SuppressWarnings("unchecked")
	public static void addHtmlAttributes(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Map<String, String> headAttributes = (HashMap<String, String>) actionContext.getObject(HtmlConstants.HTML_ATTRIBUTES);
		String key = self.getString("key");
		String value = self.getString("value");
		headAttributes.put(key, value);
	}
	
	public static void addBodyAttributes(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		Map<String, String> headAttributes = actionContext.getObject(HtmlConstants.BODY_ATTRIBUTES);
		String key = self.getString("key");
		String value = self.getString("value");
		headAttributes.put(key, value);
	}
	
	public static String generateHtml(ActionContext actionContext) throws Throwable {
		Thing self = actionContext.getObject("self");
		
		Thing htmlThing = self.doAction("getHtmlThing", actionContext);
		return ResultView.processViewThingToString(htmlThing, actionContext);
	}
}