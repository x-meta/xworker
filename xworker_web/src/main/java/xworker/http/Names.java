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
package xworker.http;

import java.util.HashMap;
import java.util.Map;

import org.xmeta.ActionContext;

/**
 * 根据键值生成唯一个名称标识。
 * 
 * @author zyx
 *
 */
public class Names {
	private Map<String, String> names = new HashMap<String, String>();
	private String namespaceId = Namespace.getNextId();
	private String name;
	
	public Names(){		
		this("xworker");
	}
	
	public Names(String name){
		this.name = name;
		if(name == null || "".equals(name.trim())){
			this.name = "xworker";
		}
	}
	
	public String get(String name){
		String value = names.get(name);
		if(value == null){
			value = Namespace.getNextId();
			names.put(name, value);
		}
		
		return value;
	}	
	
	public String getNamesapceId(){
		return namespaceId;
	}
	
	public String getNamespace(){
		return "YAHOO." + name + "." + namespaceId;
	}
	
	public static void attach(ActionContext actionContext){
		if(actionContext.getScope(0).get("_names") == null){
			Names names = new Names();
			actionContext.put("_names", names);
		}
	}
}