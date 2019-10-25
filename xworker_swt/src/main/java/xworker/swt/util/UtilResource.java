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
package xworker.swt.util;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class UtilResource {
	/**
	 * 创建图片、颜色等资源。
	 * 
	 * @param value
	 * @param descriptor
	 * @param attributeName
	 * @param actionContext
	 * @return
	 */
	public static Object createResource(String value, String descriptor, String attributeName, ActionContext actionContext){
		if(value == null || "".equals(value)){
	        return null;
	    }
		    
	    Thing resThing = new Thing(descriptor);
	    resThing.set(attributeName, value);
	    return resThing.doAction("create", actionContext);
	}
}