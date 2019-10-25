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
package xworker.ui.editor;

public class UtilThingDoc {
	static UtilThingDoc instance = new UtilThingDoc();
	public static UtilThingDoc getInstance(){
		return instance;
	}
	
	/**
	 * 取文档的第一行。
	 * 
	 * @param description 描述者
	 * @return 第一行
	 */
	public String get(String description){
		if(description == null){
			return "";
		}
		
		//取段落
		description = description.trim();
		description = subBefore(description, "<p/>");
		description = subBefore(description, "<P/>");
		description = subBefore(description, "<p />");
		description = subBefore(description, "<P />");
		description = subBefore(description, "</p>");
		description = subBefore(description, "</P>");
		description = subBefore(description, "<br/>");
		description = subBefore(description, "<br />");
		description = subBefore(description, "<BR/>");
		description = subBefore(description, "<BR />");
		description = subBefore(description, "<Br/>");
		description = description.replaceAll("(<p>)", "");
		description = description.replaceAll("(<P>)", "");
		
		return description;		
	}
	
	public String subBefore(String str, String p){
		int index = str.indexOf(p);
		if(index != -1){
			return str.substring(0, index);
		}else{
			return str;
		}
	}
}