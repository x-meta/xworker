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

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

/**
 * 映射目录对应事物的Servlet。
 * 
 * @author zhangyuxiang
 *
 */
public class HttpDirMappingServlet extends HttpServlet{
	private static final long serialVersionUID = -7340146943865363473L;
	private static Logger log = LoggerFactory.getLogger(HttpServletDo.class);

	private World world = World.getInstance();
	public static boolean debug = true;
	private String categoryPath = null;
	
	public HttpDirMappingServlet(){
		super();		
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
        //初始化World
		categoryPath = config.getInitParameter("categoryPath");		
	}


	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long start = System.currentTimeMillis();
		long startN = System.nanoTime();
		
		String uri = request.getRequestURI();
		try{
			//获取包路径			
			String servletPath = request.getServletPath();
			String thingPath = null;
			if(uri.startsWith(servletPath)){
				thingPath = uri.substring(servletPath.length(), uri.length());
			}else{
				thingPath = uri;
			}
			if(thingPath.startsWith("/")){
				thingPath = thingPath.substring(0, thingPath.length());
			}
			thingPath = thingPath.replace('/', '.');
			if(categoryPath != null && !"".equals(categoryPath)){
				thingPath = categoryPath + "." + thingPath;
			}
			
			//对应的WEB事物
			Thing webControl = world.getThing(thingPath);		
			if(webControl == null){		
				response.setContentType("text/plain; charset=utf-8");
				response.getWriter().print("webControl不存在：" + uri);
				log.warn("webControl不存在：" + uri);
				return;
			}
			
			ActionContext actionContext = new ActionContext();
			
			//基本变量
			actionContext.put("world", world);			
			//访问变量
			actionContext.put("requestBean", new HttpRequestBean(request));
			//http变量
			actionContext.put("request", request);
			actionContext.put("response", response);	
			actionContext.put("session", request.getSession());		
			//名称空间变量
			Names.attach(actionContext);
			//执行脚本
			try{
				webControl.doAction("httpDo", actionContext);
			}catch(Exception e){
				if(!(e instanceof ServletException)){
					throw new ServletException(e);
				}else{
					throw (ServletException) e;
				}
			}
		}finally{
			if(debug && log.isInfoEnabled()){
				log.info("web control time: " + (System.currentTimeMillis() - start) + ":" + (System.nanoTime() - startN) + "  " + uri);
			}
		}
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}