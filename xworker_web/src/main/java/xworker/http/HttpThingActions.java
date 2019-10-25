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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadException;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class HttpThingActions {
	@SuppressWarnings("unchecked")
	public static String doAction(ActionContext actionContext) throws ClassNotFoundException, FileUploadException, InstantiationException, IllegalAccessException, ServletException{
		Thing self = (Thing) actionContext.get("self");
		HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
		HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
		
		String key = "__httpAction__";
		HttpAction action = (HttpAction) self.getData(key);
		if(action == null){
			Class<HttpAction> cls = (Class<HttpAction>) Class.forName(self.getString("classPath"));
			action = cls.newInstance();
			self.setData(key, action);
		}
        
        //执行
		return action.doService(request, response, actionContext);
	}
	
	@SuppressWarnings("unchecked")
	public static Object doHttpMethod(ActionContext actionContext) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, SecurityException{
		Thing self = (Thing) actionContext.get("self");
		HttpServletRequest request = (HttpServletRequest) actionContext.get("request");
		HttpServletResponse response = (HttpServletResponse) actionContext.get("response");
		
		String key = "__httpActionMethod__";
		Method action = (Method) self.getData(key);
		if(action == null){
			Class cls = Class.forName(self.getString("classPath"));
			action = cls.getDeclaredMethod(self.getString("method"), HttpServletRequest.class, HttpServletResponse.class, ActionContext.class);
			self.setData(key, action);
		}
        
        //执行
		return action.invoke(null, new Object[]{request, response, actionContext});
	}
}