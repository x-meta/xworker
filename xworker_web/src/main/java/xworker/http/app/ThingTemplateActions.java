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
package xworker.http.app;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class ThingTemplateActions {
	public static String doAction(HttpServletRequest request, HttpServletResponse response, ActionContext actionContext) throws Throwable{
		Thing thing = World.getInstance().getThing(request.getParameter("thing"));
		actionContext.getScope(0).put("thing", thing);
		actionContext.getScope(0).put("template", request.getParameter("template"));
		
		String contentType = request.getParameter("contentType");
		if(contentType == null || "".equals(contentType)){
			contentType = "text/html; charset=utf-8";
		}
		actionContext.getScope(0).put("contentType", contentType);
		
		return "success";
	}
}