package xworker.io.netty.handlers.http.full;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;
import org.xmeta.ui.session.SessionManager;
import org.xmeta.util.ThingRegistor;
import org.xmeta.util.UtilData;
import org.xmeta.util.UtilString;

import freemarker.template.TemplateException;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;
import xworker.io.netty.handlers.http.HttpSession;
import xworker.io.netty.handlers.http.RequestBean;
import xworker.io.netty.handlers.http.WebSessionManager;
import xworker.security.SecurityManager;
import xworker.util.UtilTemplate;


public class SimpleControl {
	private static World world = World.getInstance();
	public static boolean debug = true;
	private static long lastCheckGlbalConfigTime = 0;
	private static WebSessionManager sessionManager = new WebSessionManager();
	private static final String[] supportSurfixs = new String[]{".do", ".ac", ".dml", ".xer", ".xer.txt", ".xer.xml"};
	private static boolean allowUnderLine = false;
	/** 允许访问的事物管理器列表，如果不在这个列表，则不允许访问 */
	private static List<String> allowThingManagers = new ArrayList<String>();
	
	public static Object doRequest(ActionContext actionContext) {
		long start = System.currentTimeMillis();
		//long startN = System.nanoTime();
		Thing self = actionContext.getObject("self");
		
		try {
			SessionManager.setLocalSessionManager(sessionManager);
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
			
			HttpSession session = actionContext.getObject("session");			
			sessionManager.setSession(session, actionContext);
			
			RequestBean requestBean = actionContext.getObject("requestBean");
			
			String uri = actionContext.getObject("uri");
			String contextPath = self.doAction("getContextPath", actionContext);
			requestBean.setContextPath(contextPath);
			actionContext.peek().put("contextPath", contextPath);
			uri = uri.substring(contextPath.length(), uri.length());
			Thing webControl = null;
			String webControlName = null;
			if(UtilData.isTrue(self.doAction("isControls", actionContext))) {
				int index = uri.indexOf("?");
				if(index > 0) {
					uri = uri.substring(0, index);
				}
				webControlName = requestBean.getParameter("sc");
				if(webControlName != null) {
					webControl = world.getThing(webControlName);
				}else{
					if(uri.startsWith("/do/")) {
						String path = uri.substring(4, uri.length());
						index = path.indexOf("!");
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
			}else {
				webControl = self;
			}
			
			if(webControl == null){		
				FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK,
						Unpooled.copiedBuffer("Control not exists\r\n", CharsetUtil.UTF_8));
				response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
				return response;
			}else{
				//加入事物管理器的判断
				if(allowThingManagers != null && allowThingManagers.size() > 0){
					ThingManager thingManager = webControl.getMetadata().getThingManager();
					if(thingManager == null){
						return sendResponse(HttpResponseStatus.FORBIDDEN);
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
						return sendResponse(HttpResponseStatus.FORBIDDEN);
					}
				}
				
				return webControl.doAction("httpDo", actionContext, "request", requestBean);
			}
		}finally {
			SessionManager.setLocalSessionManager(null);
		}
	}
	
	private static FullHttpResponse sendResponse(HttpResponseStatus status) {
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status,
				Unpooled.copiedBuffer("Control error, status=" + status + "\r\n", CharsetUtil.UTF_8));
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
		return response;
	}
	
	public static Object doResult(ActionContext actionContext) throws IOException, TemplateException{		
		//long start = System.currentTimeMillis();
		Thing resultObject = (Thing) actionContext.get("self");
        
        //取得子区域的定义，处理区域
        List<Thing> regions = resultObject.getChilds("region");
        if(regions != null){
            for(int k=0; k<regions.size(); k++){
                Thing region = (Thing) regions.get(k);
                
                //替换region的变量
                String name = region.getString("name");
                actionContext.put(name, region.doAction("doRegion", actionContext));
            }
        }
                
        String contentType = resultObject.getString("contentType");
        if(contentType.indexOf("${") != -1){
        	contentType = UtilTemplate.processThingAttributeToString(resultObject, "contentType", actionContext);
        }
                       
        //执行结果
        String type = resultObject.getString("type"); 
        if(type.indexOf("${") != -1){
        	//type = UtilTemplate.processString(actionContext, resultObject.getMetadata().getPath() + "type", type);
        	type = UtilTemplate.processThingAttributeToString(resultObject, "type", actionContext);
        }
        List<String> contentTypes = new ArrayList<String>();
        contentTypes.add(contentType);
        return resultObject.doAction(type, actionContext, "contentType", contentTypes);
        //System.out.println("do result : " + (System.currentTimeMillis() - start));
	}
	
	public static Object freemarker(ActionContext actionContext) throws Throwable{		
		Thing resultObject = (Thing) actionContext.get("self");
		String path = resultObject.getString("value");
		if(path.indexOf("${") != -1){
			path = UtilTemplate.processString(actionContext, resultObject.getMetadata().getPath() + "value", path);
        }
		if(path == null || "".equals(path)){
			return null;
		}
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		UtilTemplate.process(actionContext, path, "freemarker", new OutputStreamWriter(bout, "UTF-8"), "UTF-8");
		
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK,
				Unpooled.wrappedBuffer(bout.toByteArray()));
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, actionContext.getObject("contentType"));
		return response;
	}
	
	public static Object dataSet(ActionContext actionContext) throws Throwable{
		
		Thing resultObject = (Thing) actionContext.get("self");
		String path = resultObject.getString("value");
		if(path.indexOf("${") != -1){
			path = UtilTemplate.processString(actionContext, resultObject.getMetadata().getPath() + "value", path);
        }
		
		int index = path.indexOf(":");
		String type = path.substring(0, index);
		String modelPath = path.substring(index + 1, path.length());
		Thing model = World.getInstance().getThing(modelPath);
		
		Long formLastModified = (Long) model.getData("_form_LastModified_" + type);
		String templateName = (String) model.getData("_form_TemplateName_" + type);	
		
		//File f = (File) formObject.getData("_form_file");
		if(templateName == null || formLastModified < model.getMetadata().getLastModified()){
	        //取表单的临时文件名
			StringBuffer fileNameBuff = new StringBuffer(World.getInstance().getPath());		
			StringBuffer tName = new StringBuffer();
			tName.append("/work/forms");
			tName.append("/" + UtilString.getThingManagerFileName(model));
			tName.append("/" + model.getRoot().getMetadata().getPath().hashCode());
			tName.append("/" + model.getMetadata().getPath().hashCode());
			tName.append("/" + model.getMetadata().getName());
			tName.append("_" + type);
			tName.append(".ftl");
			String fileName = fileNameBuff.append(tName).toString();
					
			File file = new File(fileName);
			if(!file.exists() || file.lastModified() < model.getMetadata().getLastModified()){
				if(!file.exists()){				
					File dir = file.getParentFile();
					if(!dir.exists() || !dir.isDirectory()){
						dir.mkdirs();
					}
				}
				try{
					//List formElements = UtilView.
					//System.out.println("org.xmeta.util.UtilForm:114 re formed");
					Thing formThing = (Thing) model.doAction(type, actionContext); 
					String ftl = (String) formThing.doAction("toHtml", actionContext);
					OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
					w.write(ftl);
					w.flush();
					w.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
						
			templateName = tName.toString();
			model.setData("_form_LastModified_" + type, model.getMetadata().getLastModified());
			model.setData("_form_TemplateName_" + type, templateName);
		}
		
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			UtilTemplate.process(actionContext, templateName, "freemarker", new OutputStreamWriter(bout, "UTF-8"), "UTF-8");
			
			FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK,
					Unpooled.wrappedBuffer(bout.toByteArray()));
			response.headers().set(HttpHeaderNames.CONTENT_TYPE, actionContext.getObject("contentType"));
			return response;
		}catch(Throwable t){
			model.setData("_form_LastModified_" + type, null);
			model.setData("_form_TemplateName_" + type, null);
			
			throw t;
		}
	}
	
	public static Object form(ActionContext actionContext) throws Throwable{
		Thing resultObject = (Thing) actionContext.get("self");
		String path = resultObject.getString("value");
		if(path.indexOf("${") != -1){
			path = UtilTemplate.processString(actionContext, resultObject.getMetadata().getPath() + "value", path);
        }
		Thing formObject = null;
		if(path.startsWith("$")){
			path = path.substring(1, path.length());
			Thing parent = resultObject.getParent();
			for(Thing child : parent.getAllChilds()){
				if(child.isThingByName("view") && path.equals(child.getString("name"))){
					formObject = child;
					break;
				}
			}
		}else{
			formObject = World.getInstance().getThing(path);
		}		
		if(formObject == null){
			FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK,
					Unpooled.copiedBuffer("Form thing is null.\r\n", CharsetUtil.UTF_8));
			response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=utf-8");
			return response;
		}
		
		return processViewThing(formObject, actionContext);
	}
	
	/**
	 * 处理表单事物生成Freemaker模板，并返回模板的路径。
	 * 
	 * @param formObject
	 * @param actionContext
	 * @return
	 * @throws Throwable
	 */
	public static String processViewThingTemplate(Thing formObject, ActionContext actionContext) throws Throwable{
		Long formLastModified = (Long) formObject.getData("_form_LastModified_");
		String templateName = (String) formObject.getData("_form_TemplateName_");	
		//File f = (File) formObject.getData("_form_file");
		if(formLastModified == null || templateName == null || formLastModified < formObject.getMetadata().getLastModified()){
	        //取表单的临时文件名
			StringBuffer fileNameBuff = new StringBuffer(World.getInstance().getPath());		
			StringBuffer tName = new StringBuffer();
			tName.append("/work/forms");
			tName.append("/" + UtilString.getThingManagerFileName(formObject));
			tName.append("/" + formObject.getRoot().getMetadata().getPath().replace('.', '/'));
			tName.append("/" + formObject.getMetadata().getPath().hashCode());
			tName.append("/" + formObject.getMetadata().getName());
			tName.append(".ftl");
			String fileName = fileNameBuff.append(tName).toString();
					
			File file = new File(fileName);
			if(!file.exists() || file.lastModified() < formObject.getMetadata().getLastModified()){
				if(!file.exists()){				
					File dir = file.getParentFile();
					if(!dir.exists() || !dir.isDirectory()){
						dir.mkdirs();
					}
				}
				try{
					//List formElements = UtilView.
					//System.out.println("org.xmeta.util.UtilForm:114 re formed");
					String ftl = (String) formObject.doAction("toHtml", actionContext);
					OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
					w.write(ftl);
					w.flush();
					w.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
						
			templateName = tName.toString();			
			//formObject.setData("_form_file", file);
			formObject.setData("_form_LastModified_", formObject.getMetadata().getLastModified());
			formObject.setData("_form_TemplateName_", templateName);
			
			UtilTemplate.removeTempalteCache(templateName);
		}
		
		return templateName;
	}
	
	public static String processViewThingToString(Thing formObject, ActionContext actionContext) throws Throwable{
		String templateName = processViewThingTemplate(formObject, actionContext);
		
		try{
			return UtilTemplate.process(actionContext, templateName, "freemarker","UTF-8");
		}catch(Throwable t){
			formObject.setData("_form_LastModified_", null);
			formObject.setData("_form_TemplateName_", null);
			
			throw t;
		}
	}
	
	public static Object processViewThing(Thing formObject, ActionContext actionContext) throws Throwable{
		String templateName = processViewThingTemplate(formObject, actionContext);
		
		try{
//			if(formObject.getBoolean("noCache")){
//				UtilTemplate.removeTempalteCache(templateName);
//			}
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			UtilTemplate.process(actionContext, templateName, "freemarker", new OutputStreamWriter(bout, "UTF-8"), "UTF-8");
			//bout.write("\r\n".getBytes());
			FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK,
					Unpooled.wrappedBuffer(bout.toByteArray()));
			response.headers().set(HttpHeaderNames.CONTENT_TYPE, actionContext.getObject("contentType"));
			return response;
		}catch(Throwable t){
			formObject.setData("_form_LastModified_", null);
			formObject.setData("_form_TemplateName_", null);
			
			throw t;
		}
	}
	
	public static Object doRegion(ActionContext actionContext) throws IOException{
		Thing region = (Thing) actionContext.get("self");
		String type = region.getString("type");        
        
		return region.doAction(type, actionContext);
	}
	
	public static Object redirect(ActionContext actionContext) throws IOException, TemplateException{
        Thing self = (Thing) actionContext.get("self");
        
        String url = self.getString("value");
        if(url.indexOf("${") != -1){
            url = UtilTemplate.processString(actionContext, self.getMetadata().getPath()+ "value", url);
        } 
       
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.FOUND, Unpooled.EMPTY_BUFFER);
        response.headers().set(HttpHeaderNames.LOCATION, url);
        
        return response;
    }
	
	public static Object control(ActionContext actionContext) throws IOException, TemplateException{
    	Thing self = (Thing) actionContext.get("self");
    	
        String url = self.getString("value");
        if(url.indexOf("${") != -1){
            url = UtilTemplate.processString(actionContext, self.getMetadata().getPath() + "value", url);
        }
        
    	FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK,
    			Unpooled.copiedBuffer("Control not found, path=" + self.getString("value") + ".\r\n", CharsetUtil.UTF_8));
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=utf-8");
		return response;
    }
	
	public static Object template(ActionContext actionContext) throws IOException, TemplateException{
    	Thing self = (Thing) actionContext.get("self");
    	
        String templateThingPath = self.getString("value");
        if(templateThingPath.indexOf("${") != -1){
            templateThingPath = UtilTemplate.processString(actionContext, self.getMetadata().getPath() + "value", templateThingPath);
        }  
        Thing templateObject = World.getInstance().getThing(templateThingPath);
        //Bindings bindings = actionContext.peek();
        if(templateObject != null){
        	//bindings.setVariable("____templateWriter", ctxs.httpContext.writer);
            templateObject = templateObject.detach();
            //templateObject.setAttribute("output", "____templateWriter");
            String text = templateObject.doAction("process", actionContext);
            
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK,
        			Unpooled.copiedBuffer(text + ".\r\n", CharsetUtil.UTF_8));
			response.headers().set(HttpHeaderNames.CONTENT_TYPE, actionContext.getObject("contentType"));
			return response;
        }else{
        	FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK,
        			Unpooled.copiedBuffer("Control not found, path=" + self.getString("value") + ".\r\n", CharsetUtil.UTF_8));
			response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=utf-8");
			return response;
        }
    }
	
	public static Object doCheckLogin(ActionContext actionContext) throws IOException{
		Thing self = (Thing) actionContext.get("self");
		HttpSession session = (HttpSession) actionContext.get("session");
		String name = self.getStringBlankAsNull("loginAttribute");
		if(name == null){
			return true;
		}else{
			if(session.getAttribute(name) != null){
				return true;
			}else{
				FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.FOUND, Unpooled.EMPTY_BUFFER);
		        response.headers().set(HttpHeaderNames.LOCATION, self.getString("loginUrl"));
		        
		        return response;				
			}
		}
	}
	
	public static boolean checkPermission(Thing self, ActionContext actionContext){
		if(self.getBoolean("checkPermission")){
			return SecurityManager.doCheck("WEB", self.getString("permission"), self.getString("action"), self.getMetadata().getPath(), actionContext);
		}else{
			return true;
		}
	}
	
	public static Object httpDo(ActionContext actionContext) throws Exception{
		//获取SimpleControl框架配置本身
        Thing self = (Thing) actionContext.get("self");
        
        //权限检查
        if(!checkPermission(self, actionContext)){
        	return null;
        }
        
        //登录校验
        if(self.getBoolean("checkLogin")){
        	Object obj = self.doAction("doCheckLogin", actionContext);
        	if(obj instanceof Boolean && (Boolean) obj == false){
        		return null;
        	}
        }
        
        String result = "success";
        //执行业务逻辑，并返回结果
        Object r = self.doAction("doAction", actionContext);
        if(r instanceof HttpResponse) {
        	return r;
        }
        
        if(r instanceof String){
            result = (String) r;
        }
        
        //寻找并处理结果
        List<Thing> results = self.getChilds("result");
		Thing resultObject = null;
		for(int i=0; i<results.size(); i++){
			Thing rObject = results.get(i);
			if(rObject.getMetadata().getName().equals(result)){	
				resultObject = rObject;
                break;
			}
		}
		
		//执行结果的方法，相当于输出界面
		if(resultObject != null){
			return resultObject.doAction("doResult", actionContext);
		}
		
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK,
    			Unpooled.copiedBuffer(result + ".\r\n", CharsetUtil.UTF_8));
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=utf-8");
		return response;
       
    }
}
