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

public class EasyUIFunction {
	/**
	 * EasyUI函数转换成JavaScript。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static String toJavaScriptCode(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
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
			String params = self.getStringBlankAsNull("params");			
			return "function(" + (params != null ? params : "") + "){\n    " + HtmlUtil.getIdentString(script, "    ") + "\n}"; 
		}else{
			return null;
		}
	}
}