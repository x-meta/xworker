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
import org.xmeta.util.UtilMap;

import freemarker.template.TemplateException;
import xworker.util.UtilTemplate;


public class HtmlUtil {
	/**
	 * 控件需要引入的脚本或者其他什么的需要放在HTML的header标签内的。
	 * 
	 * @param key 关键字
	 * @param html HTML代码
	 * @param actionContext 变量上下文
	 */
	@SuppressWarnings("unchecked")
	public static void addHeader(String key, String html, ActionContext actionContext){
		List<Map<String, String>> headers =  (List<Map<String, String>>) actionContext.get("heads");
					
		if(headers != null){
            boolean have = false;
            for(Map<String, String> head : headers){
                if(key.equals(head.get("name"))){
                    have = true;
                    break;
                }        
            }
            
            if(!have){
            	Map<String, String> head = new HashMap<String, String>();
            	head.put("name", key);
            	head.put("value", html);
            	headers.add(head);
            }
		}
	}
	
	/**
	 * 添加到底部生成html的事物。
	 * 
	 * @param thing 事物
	 * @param actionContext 变量上下文
	 */
	@SuppressWarnings("unchecked")
	public static void addBottomThing(Thing thing, ActionContext actionContext){
		List<Thing> bottoms =  (List<Thing>) actionContext.get("bottoms");
		
		if(bottoms != null){
			bottoms.add(thing);
		}
	}
	
	/**
	 * 添加底步的JavaScript，一般生成到最底下。子代码使用xworker.html.base.scripts.JavaScript/@Code事物模型。
	 * 
	 * @param thing 事物
	 * @param actionContext 变量上下文
	 */
	public static void addBottomJavaScriptThing(Thing thing, ActionContext actionContext){
		Thing scriptThing =  (Thing) actionContext.get(HtmlConstants.HTML_BOTTOM_JAVASCRIPT_THING);
		
		if(scriptThing != null){
			scriptThing.addChild(thing, false);
		}
	}
	
	/**
	 * 添加底部的 HTML代码。
	 * 
	 * @param key
	 * @param html
	 * @param actionContext
	 */
	@SuppressWarnings("unchecked")
	public static void addBottom(String key, String html, ActionContext actionContext){
		List<Map<String, String>> bottoms =  (List<Map<String, String>>) actionContext.get("bottoms");
					
		if(bottoms != null){
            boolean have = false;
            for(Map<String, String> bottom : bottoms){
                if(key.equals(bottom.get("name"))){
                    have = true;
                    break;
                }        
            }
            
            if(!have){
            	Map<String, String> bottom = new HashMap<String, String>();
            	bottom.put("name", key);
            	bottom.put("value", html);
            	bottoms.add(bottom);
            }
		}
	}
	
	public static String getChildsHtml(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
    	
        String html = "";
        for(Thing child : self.getAllChilds()){
            String htmlCode = (String) child.doAction("toHtml", actionContext);
            if(htmlCode != null &&  !"".equals(htmlCode)){
                html = html + "\n" + htmlCode;
            }
        }
        return html;
	}
	
	public static Object precessTemplate(ActionContext actionContext, String selfObjName, String templatePath) throws Throwable{
		 Object self = actionContext.get("self");
		 return UtilTemplate.process(UtilMap.toMap(new Object[]{selfObjName,self}), templatePath, "freemarker", "utf-8");
	}
	
	public static String getJsString(String value) throws IOException, TemplateException{
        if(value == null || "".equals(value)){
            return "";
        }
            
        if(value.startsWith("'")){
            value = value.substring(1, value.length());
        }
        if(value.endsWith("'")){
            value = value.substring(0, value.length() - 1);
        }
        
        value = UtilTemplate.processString(UtilMap.toMap(new Object[]{"text", value}), "${text?js_string}");
        
        return "'" + value + "'";
    }
	
	public static String getHtmlString(String value) throws Throwable{
		if(value == null){
			return value;
		}else{
			return UtilTemplate.process(UtilMap.toMap(new Object[]{"str", value}), "xworker/ide/worldExplorer/swt/dialogs/StringToHtml.ftl", "freemarker");
		}
	}
	
	/**
	 * 如果一个字符串是多行的，那么除了第一行其他每行前面加上缩进。
	 * 
	 * @param str
	 * @param ident
	 * @return
	 */
	public static String getIdentString(String str, String ident){
		if(str == null || ident == null){
			return str;
		}
		
		String sident = null;
		for(String s : str.split("[\n]")){
			if(sident != null){
				sident = sident + "\n";			
				sident = sident + ident + s;				
			}else{
				sident = s;
			}
		}
		
		return sident;
	}
	
	/**
	 * 取属性的值，或者取指定子事物的动作的返回值。
	 * 
	 * @param thing
	 * @param attribute
	 * @param childThingName
	 * @param childMethod
	 * @param ident
	 * @return
	 */
	public static String getAttributeOrChildThingCode(Thing thing, String attribute, String childThingName, String childMethod, String ident){
		String code = thing.getString(attribute);
		if(code == null){
			Thing child = thing.getThing(childThingName + "@0");
			if(child != null){
				code = (String) child.doAction(childMethod);
			}
		}
		if(code == null){
			code = "";
		}
		
		String r = null;
		for(String ln : code.split("[\n]")){
			if(r == null){
				r = ln;
			}else{
				r = r + "\n" + ident + ln;
			}
		}
		
		return r;
	}
	
	public static String getAttr(Thing self, String attrName, String tagName){
		String value = self.getStringBlankAsNull(attrName);
		if(value != null){
			return " " + tagName + "=\"" + value + "\"";
		}else{
			return "";
		}
	}
	
	/**
	 * 返回属性的字符串，如果为空返回空字符串。
	 * 
	 * @param self
	 * @param attrName
	 * @return
	 */
	public static String getAttr(Thing self, String attrName){
		String value = self.getStringBlankAsNull(attrName);
		if(value != null){
			return value;
		}else{
			return "";
		}
	}
	
	/**
	 * 根据属性的布尔值返回trueValue或falseValue。
	 * 
	 * @param self
	 * @param attrName
	 * @param trueValue
	 * @param falseValue
	 * @return
	 */
	public static String getAttrBoolean(Thing self, String attrName, String trueValue, String falseValue){
		String value = self.getStringBlankAsNull(attrName);
		if(value != null){
			if(self.getBoolean(attrName)){
				return trueValue;
			}else{
				return falseValue;
			}
		}else{
			return "";
		}
	}
}