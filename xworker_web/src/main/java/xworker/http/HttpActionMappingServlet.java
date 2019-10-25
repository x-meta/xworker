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
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Category;
import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;

public class HttpActionMappingServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(HttpServletDo.class);
	public static Map<String, String> mappings = null;
	
	private static World world = World.getInstance();
		
	public HttpActionMappingServlet(){
		super();		
	}
	
	/**
	 * 初始化或更新映射表。
	 */
	public static synchronized void initMappings(){
		//只初始化一次
		if(mappings != null){
			return;
		}
		
		Map<String, String> newMappings = new HashMap<String, String>();
		for(ThingManager thingManager : world.getThingManagers()){
			for(Category lv1Category : thingManager.getCategory(null).getCategorys()){
				for(Category lv2Category : lv1Category.getCategorys()){
					Thing thing = world.getThing(lv2Category.getName() + ".config.WebControls");
					if(thing != null){
						getMappings(newMappings, thing);
					}
				}
			}
		}
		
		mappings = newMappings;
	}
	
	public static void initMappings(Thing thing){
		if(mappings == null){
			initMappings();
		}
		
		getMappings(mappings, thing);
	}
	
	private static void getMappings(Map<String, String> mappings, Thing thing){
		for(Thing child : thing.getChilds("Mapping")){
			mappings.put(child.getString("path") + ".ac", child.getString("webControlPath"));
		}
		
		for(Thing child : thing.getChilds("Mappings")){
			getMappings(mappings, child);
		}
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long start = System.currentTimeMillis();
		long startN = System.nanoTime();
		
		ActionContext actionContext = new ActionContext();
		if(mappings == null){
			Thing servletTask = world.getThing("xworker.ide.worldExplorer.background.tasks.HttpServletTask");
			if(servletTask != null){
				servletTask.doAction("run", actionContext);
			}
		}		
		
		//设置变量
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
		String webControlName = request.getRequestURI();
		
		Thing webControl = world.getThing(mappings.get(webControlName));
		
		if(webControl == null){		
			response.setContentType("text/plain; charset=utf-8");
			response.getWriter().print("webControl不存在：" + webControlName);
			log.warn("webControl不存在：" + webControlName);
			//throw new ServletException();
		}else{
			try{
				webControl.doAction("httpDo", actionContext);
			}catch(Exception e){
				if(!(e instanceof ServletException)){
					throw new ServletException(e);
				}else{
					throw (ServletException) e;
				}
			}
		}
		if(HttpServletDo.debug && log.isInfoEnabled()){
			log.info("web control time: " + (System.currentTimeMillis() - start) + ":" + (System.nanoTime() - startN) + "  " + webControlName);
		}
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}