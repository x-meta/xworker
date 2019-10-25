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

import org.xmeta.ActionContext;
import org.xmeta.Bindings;

public class Namespace {
	static long currentId = 0;
	
	public static synchronized String getNextId(){
		if(currentId == Long.MAX_VALUE){
			currentId = 0;
		}
		
		currentId ++;
		
		String nextId = "x" + currentId;
		nextId = nextId.replace('-', '_');
		return nextId;
	}
	
	/**
	 * 返回指定动作上下文的名称空间名，从动作上下文的全局0级变量中找。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static String getNamespaceId(ActionContext actionContext){
		Bindings rootBindings = actionContext.getScope(0);
		String namespaceId = (String) rootBindings.get("_html_name_sapce_name_");
		if(namespaceId == null){
			namespaceId = getNextId();
			rootBindings.put("_html_name_sapce_name_", namespaceId);
		}
		
		return namespaceId;
	}
	
	/**
	 * 返回名称空间的全名，YAHOO.xworker.&lt;namespaceId&gt;。
	 * 
	 * @param actionContext
	 * @return
	 */
	public static String getNamesapce(ActionContext actionContext){
		String namespaceId = getNamespaceId(actionContext);
		
		return "YAHOO.xworker." + namespaceId;
	}
}