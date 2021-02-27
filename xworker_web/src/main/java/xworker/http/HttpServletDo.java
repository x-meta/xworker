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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;
import org.xmeta.ui.session.SessionManager;
import org.xmeta.util.ThingRegistor;

import xworker.lang.Configuration;
import xworker.lang.executor.Executor;

/**
 * 执行事物的Servlet。
 * 
 * @author zhangyuxiang
 *
 */
public class HttpServletDo extends HttpServlet{
	private static final long serialVersionUID = -7340146943865363473L;
	private static final String TAG = HttpServletDo.class.getName();

	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String DELETE = "DELETE";
	public static final String HEAD = "HEAD";
	public static final String OPTIONS = "OPTIONS";
	public static final String PUT = "PUT";
	public static final String TRACE = "TRACE";
	
	private World world = World.getInstance();
	public static boolean debug = true;
	private long lastCheckGlbalConfigTime = 0;
	private static final String[] supportSurfixs = new String[]{".do", ".ac", ".dml", ".xer", ".xer.txt", ".xer.xml"};
	/** 允许访问的事物管理器列表，如果不在这个列表，则不允许访问 */
	private static List<String> allowThingManagers = new ArrayList<String>();	
	
	private static WebSessionManager sessionManager = new WebSessionManager();
	
	boolean allowUnderLine = false;
	
	public HttpServletDo(){
		super();		
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		//初始化World			
		if(!world.isInited()) {
			String xworkerPath = config.getInitParameter("xworkerPath");	
			Executor.info(TAG, "XWorker path param=" + xworkerPath);
			if(xworkerPath != null  && !"".equals(xworkerPath) && !world.isInited()){
				if("{contextPath}".equals(xworkerPath)) {
					xworkerPath = "./xworkers" + config.getServletContext().getContextPath();
				}
				
				File file =new File(xworkerPath);
				if(file.exists() == false) {
					file.mkdirs();
				}
				world.init(xworkerPath);
				world.setWebFileRoot(config.getServletContext().getRealPath("/"));
			} else {
				//初始化默认在WEB-INF/data/
				//String worldPath = config.getServletContext().getRealPath("WEB-INF/data/");
				
				world.init(null);
				world.setWebFileRoot(config.getServletContext().getRealPath("/"));
			}
		}
		
		//设置GlobalConfig的webUrl，这样应用内就不需要再初始化了
		
		String contextPath = config.getServletContext().getContextPath();
		if(!contextPath.endsWith("/")) {
			contextPath = contextPath + "/";
		}
		try {
			Class<?> cls = world.getClassLoader().loadClass("xworker.util.GlobalConfig");
			Method setWebUrl = cls.getDeclaredMethod("setWebUrl", String.class, boolean.class);
			setWebUrl.invoke(null, contextPath, true);
			Executor.info(TAG, "GlobalConfig url setted: url=" + contextPath);
		}catch(Exception e) {
			Executor.info(TAG, "GlobalConfig url set exception", e);
		}
		Enumeration<String> names = config.getInitParameterNames();
		Executor.info(TAG, "init parameter names = " + names);
		while(names.hasMoreElements()){
			String name = names.nextElement();
			String prefix = "thingManager-";
			if(name.startsWith(prefix)){
				String filePath = config.getInitParameter(name);
				File file = new File(filePath);
				if(file.exists()){
					ThingManager thingManager = world.initThingManager(file);
					if(thingManager != null){
						Executor.info(TAG, "Thing manager inited: name=" + name + ", path=" + filePath);
					}else{
						Executor.info(TAG, "Thing manager init failed: name=" + name + ", path=" + filePath);
					}
				}else{
					Executor.info(TAG, "file not exists, path=" + filePath);
				}
			}else{
				Executor.info(TAG, "param name not started with thingManager-, name=" + name);
			}
		}
		
		//允许访问的事物管理器列表
		String thingManagers = config.getInitParameter("thingManagers");	
		if(thingManagers != null){
			for(String th : thingManagers.split("[,]")){
				th = th.trim();
				if(!"".equals(th)){
					allowThingManagers.add(th);
				}
			}
		}
		
		//把根目录也加入到ThingManager中
		String path = config.getServletContext().getRealPath("/");
		File file = new File(path);
		if(file.exists()){
			try{
				World.getInstance().addFileThingManager(World.WEBROOT_TEMP_THINGMANAGER, file, false, false);
				Executor.info(TAG, "webroot added to thingmanager");
			}catch(Exception e){
				Executor.warn(TAG, "add webroot to thingmanager error", e);
			}
		}else{
			Executor.info(TAG, "webroot is not a file directory, not add to thingmanager, path=" + path);
		}
				
		if("true".equals(config.getInitParameter("allowUnderLine"))){
			Executor.info(TAG, "allowUnderLine=true");
			allowUnderLine = true;
		}else{
			Executor.info(TAG, "allowUnderLine=false");
		}
		
		//默认的profile
		String configurationProfile = config.getInitParameter("profile");
		if(configurationProfile != null) {
			Configuration.setProfile(configurationProfile);
		}
		
		//配置模型，如果存在执行它的init方法
		String configurationPath = config.getInitParameter("init");
		if(configurationPath != null) {
			Thing configuration = World.getInstance().getThing(configurationPath);
			if(configuration != null) {
				configuration.getAction().run();
			}
		}		
		
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doRequest(request, response, HttpServletDo.GET);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doRequest(req, resp, HttpServletDo.DELETE);
	}

	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doRequest(req, resp, HttpServletDo.HEAD);
	}

	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doRequest(req, resp, HttpServletDo.OPTIONS);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doRequest(req, resp, HttpServletDo.PUT);
	}

	@Override
	protected void doTrace(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doRequest(req, resp, HttpServletDo.TRACE);
	}

	public void doRequest(HttpServletRequest request, HttpServletResponse response, String method) throws ServletException, IOException {
		long start = System.currentTimeMillis();
		long startN = System.nanoTime();

		try {
			SessionManager.setLocalSessionManager(HttpServletDo.sessionManager);
			if(start - lastCheckGlbalConfigTime > 10000){
				lastCheckGlbalConfigTime = start;
				//最多10秒检测一次全局配置
				Thing cfg = world.getThing(ThingRegistor.getPath("_xworker_globalConfig"));
				if(cfg != null){
					debug = cfg.getBoolean("webDebug");
				}else{
					debug = false;
				}
			}
			
			ActionContext actionContext = new ActionContext();
			//设置变量
			//基本变量
			actionContext.put("world", world);			
			
			//访问变量
			actionContext.put("requestBean", new HttpRequestBean(request));
			
			//http变量
			actionContext.put("request", request);
			actionContext.put("requestMethod", method);
			actionContext.put("response", response);	
			actionContext.put("servlet", this);				
			actionContext.put("session", request.getSession());		
			
			HttpServletDo.sessionManager.setSession(request, actionContext);
			
			//名称空间变量
			Names.attach(actionContext);
					
			//执行脚本
			String uri = request.getRequestURI();
			String contextPath = request.getContextPath();
			uri = uri.substring(contextPath.length(), uri.length());
			Thing webControl = null;
			String webControlName = null;
			if(uri.length() == 3){
				webControlName = request.getParameter("sc");
				webControl = world.getThing(webControlName);
			}else{
				if(uri.startsWith("/do/")) {
					String path = uri.substring(4, uri.length());
					int index = path.indexOf("!");
					if(index != -1) {
						path = path.substring(0, index);
					}
					path = path.replace('/', '.');
					webControl = world.getThing(path);
				}else {
					//*.do,*.ac,*.dml等事物的映射
					for(String sur : supportSurfixs){
						if(uri.endsWith(sur)){
							webControlName = uri.substring(1,  uri.length() - sur.length());
							break;
						}
					}
					
					webControlName = webControlName.replace('/', '.');		
					if(allowUnderLine){
						//_下划线是老的应用在用，但是_也是事物的路径所允许的，因此现有版本不建议使用
						webControlName = webControlName.replace('_', '.');
					}
					webControl = world.getThing(webControlName);
				}
			}
			
			if(webControl == null){		
				response.setContentType("text/plain; charset=utf-8");
				response.getWriter().print("webControl不存在：" + webControlName);
				Executor.warn(TAG, "webControl不存在：" + webControlName);
				//throw new ServletException();
			}else{
				//加入事物管理器的判断
				if(allowThingManagers != null && allowThingManagers.size() > 0){
					ThingManager thingManager = webControl.getMetadata().getThingManager();
					if(thingManager == null){
						response.setStatus(405);
						return;
					}
					
					String thingManagerName = thingManager.getName();
					boolean ok = false;
					for(String allow : allowThingManagers){
						if(allow.equals(thingManagerName)){
							ok = true;
							break;
						}
					}
					
					if(!ok){
						response.setStatus(405);
						return;
					}
				}
				
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
			if(debug){
				Executor.info(TAG, "web control time: " + (System.currentTimeMillis() - start) + ":" + (System.nanoTime() - startN) + "  " + webControlName);
			}
		}finally {
			SessionManager.setLocalSessionManager(null);
		}
		
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doRequest(request, response, "POST");
	}
	
	/**
	 * 获取Webapp的路径。
	 * 通过项目的配置文件project可以配置webapp，每个webapp都是一个虚拟的应用，每个应用有
	 * 自己的数据库和登录信息。<p>
	 * getWebapp方法可以得到虚拟的应用的名称，默认的名称是xworker，在core插件的配置下的
	 * project定义。<p>
	 * 
	 * @param url
	 * @param contentPath
	 * @return
	 */
	public static String getWebappName(String url, String contentPath){
		if(url.length() <= contentPath.length()){
			return null;
		}
		
		String temp = url.substring(contentPath.length() + 1, url.length());
		int scriptIndex = temp.indexOf("do");
		if(scriptIndex != -1){
			temp = temp.substring(0, scriptIndex);
		}
		if(temp.length() == 0){
			return null;
		}
		
		int index = temp.indexOf("/");
		if(index == -1){
			return temp;
		}else{
			return temp.substring(0, index);
		}		
	}
	
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}
}