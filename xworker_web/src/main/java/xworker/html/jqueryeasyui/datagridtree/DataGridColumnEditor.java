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
package xworker.html.jqueryeasyui.datagridtree;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class DataGridColumnEditor {
	/**
	 * 列编辑器的toJavaScriptCode方法。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static Object toJavaScriptCode(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		
		String code = "{type:" + self.getString("type");
		String options = self.getStringBlankAsNull("options");
		if(options == null){
			Thing optionThing = self.getThing("options@0");
			if(optionThing != null && optionThing.getChilds().size() > 0){
				options = (String) optionThing.getChilds().get(0).doAction("toJavaScriptCode", actionContext);
			}
		}
		if(options != null){
			code = code + ",options:" + options;
		}
		
		return code + "}";
	}
}