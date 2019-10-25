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
package xworker.html.base.scripts;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class JavaScript {
	public static String toHtml(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
    	
		String code = "<script language=\"javascript\">";
		code = code + JavaScript.getJavaScriptCode(self, actionContext);
		code = code + "\n</script>";
		
		return code;
	}
	
	/**
	 * JavaScriptFunction的toJavaScriptCode代码。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static String javaScriptFunction(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
    	
		String params = self.getString("params");
		if(params == null){
			params = "";
		}
		String code = "function " + self.getMetadata().getName() + "(" + params + "){";
		String childCode = JavaScript.getJavaScriptCode(self, actionContext);
		if(childCode != null && !"".equals(childCode)){
			for(String line : childCode.split("[\n]")){
				code = code + "\n    " + line;
			}
		}
		code = code + "\n}";
		
		return code;
	}
	
	/**
	 * 普通代码。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static String code(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		return JavaScript.getJavaScriptCode(self, actionContext);
	}
	
	public static String getJavaScriptCode(Thing thing, ActionContext actionContext){
		String code = "";
		if(thing.getBoolean("useChildsCode")){
			for(Thing child : thing.getChilds()){
				String childCode = (String) child.doAction("toJavaScriptCode", actionContext);
				if(child.isThingByName("JavaScriptObject")){
					String type = (String) child.doAction("getJavaScriptObjectType", actionContext);
					code = code + "\nvar " + child.getString("varname") + " = new " + type + "(";
					if(childCode != null && !"".equals(childCode)){
						int index = 0;
						String[] lines = childCode.split("[\n]");
						for(String line : lines){
							if(index == 0){
								code = code + line;								
							}else if(index == lines.length - 1){
								code = code + "\n" + line;
							}else{
								code = code + "\n" + "    " + line;
							}
							
							index++;
						}
					}
					code = code + ");";
				}else{
					if(childCode != null && !"".equals(childCode)){
						for(String line : childCode.split("[\n]")){
							code = code + "\n" + line;
						}
					}
				}
			}
		}else{
			String selfCode = thing.getString("code");
			if(selfCode != null && !"".equals(selfCode)){
				for(String line : selfCode.split("[\n]")){
					code = code + "\n" + line;
				}
			}
		}
		
		return code;
	}
}