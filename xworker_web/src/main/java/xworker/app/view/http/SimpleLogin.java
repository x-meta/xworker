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
package xworker.app.view.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

import xworker.http.HttpAction;


/**
 * 简单的登录。
 * 
 * @author Administrator
 *
 */
public class SimpleLogin implements HttpAction{

	@Override
	public String doService(HttpServletRequest request,
			HttpServletResponse response, ActionContext actionContext)
			throws ServletException {
		HttpSession session = request.getSession();
		Thing self = (Thing) actionContext.get("loginThing");		
		String cmd = request.getParameter("command");
		String sessionAttributeName = self.getString("sessionAttributeName");
		if("login".equals(cmd)){
			//执行doLogin方法
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("name", request.getParameter("name"));
			params.put("password", request.getParameter("password"));
			params.put("request", request);
			params.put("response", response);
			params.put("session", session);
			self.doAction("doLogin", actionContext, params);
		}else if("logout".equals(cmd)){
			//设置登录信息为null
			session.setAttribute(sessionAttributeName, null);
			try {
				response.sendRedirect("do?sc=" + self.getMetadata().getPath());
			} catch (IOException e) {
				throw new ServletException("SimpleLogin send redirect error", e);
			}
			
			return null;
		}
		
		if(session.getAttribute(sessionAttributeName) == null){
			//显示登录页面
			response.setContentType("text/html; charset=utf-8");
			try{
				actionContext.peek().put("outputStream", response.getOutputStream());
				self.doAction("toLoginHtml", actionContext);
			} catch (IOException e) {
				throw new ServletException("SimpleLogin send login page error", e);
			}
		}else{
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("name", request.getParameter("name"));
			params.put("password", request.getParameter("password"));
			params.put("request", request);
			params.put("response", response);
			params.put("session", session);
			
			return (String) self.doAction("doLogined", actionContext, params); 
		}
		
		return null;
	}
		
	public static Object doLogined(ActionContext actionContext) throws ServletException{
		Thing self = (Thing) actionContext.get("self");
		//跳转到登录后的页面
		try {
			String loginedUrl = self.getString("loginedUrl");
			HttpServletResponse response  = (HttpServletResponse) actionContext.get("response");
			if(loginedUrl != null && !"".equals(loginedUrl)){
				response.sendRedirect(loginedUrl);
			}else{
				response.getWriter().println("loginedUrl is null");
			}
		} catch (IOException e) {
			throw new ServletException("SimpleLogin send redirect error", e);
		}
		
		return null;
	}
	public static void doLogin(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String sessionAttributeName = self.getString("sessionAttributeName");
		Object user = self.doAction("loadUser", actionContext);
		if(user != null){
			HttpSession session = (HttpSession) actionContext.get("session");
			session.setAttribute(sessionAttributeName, user);
		}
	}
	
	public static void loadUser(ActionContext actionContext){
		throw new ActionException("Please implement loadUser method");
	}
	
	public static Object getSimpleLoginThing(ActionContext actionContext){
		return actionContext.get("self");
	}

}