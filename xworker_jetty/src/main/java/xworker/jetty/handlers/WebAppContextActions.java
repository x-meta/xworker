package xworker.jetty.handlers;

import javax.servlet.ServletException;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;

import xworker.http.WebsocketJavax;

public class WebAppContextActions {
	public static Object create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Server server = actionContext.getObject("server");
		String webApp = self.doAction("getWebApp", actionContext);
		String contextPath = self.doAction("getContextPath", actionContext);
		String resourceBase = self.doAction("getResourceBase", actionContext);
		String[] welcomeFiles = self.doAction("getWelcomeFiles", actionContext);
		
		WebAppContext context = new WebAppContext(webApp, contextPath);
		context.setServer(server);
	    
		if(resourceBase != null && !"".equals(resourceBase)) { 
			context.setResourceBase(resourceBase);
		}
	    
		if(welcomeFiles != null) {
	        context.setWelcomeFiles(welcomeFiles);
		}
		
		if(contextPath != null && !"".equals(contextPath)) {
			context.setContextPath(contextPath);
		}
				
		if(self.getBoolean("useXWorkerClassLoader")) {
			context.setClassLoader(World.getInstance().getClassLoader());
		}
		
	    context.setAttribute("org.eclipse.jetty.servlet.Default.dirAllowed", UtilData.isTrue(
	    		self.doAction("isDirAllowed", actionContext)));
	    
	    //SWT rwt会出现文件明明存在，但404问题，加下面的语句可以解决
	    context.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
	    
	    //执行初始化
	    self.doAction("init", actionContext, "context", context);
	    
	    return context;
	}
	
	public static void createWebSocket(ActionContext actionContext) throws ServletException {
		Thing self = actionContext.getObject("self");
		
		WebAppContext context = actionContext.getObject("context");
		
		ServerContainer serverContainer = WebSocketServerContainerInitializer.configureContext(context);
		for(Thing child : self.getChilds()) {
			child.doAction("create", actionContext, "serverContainer", serverContainer);
		}
	}
	
	public static void createEndpoint(ActionContext actionContext) throws DeploymentException {
		Thing self = actionContext.getObject("self");
		
		ServerContainer serverContainer = actionContext.getObject("serverContainer");
		Class<?> cls = self.doAction("getEndpointClass", actionContext);
		if(cls != null) {
			serverContainer.addEndpoint(cls);
		}
	}
	
	public static void createXWorkerEndpoint(ActionContext actionContext) throws DeploymentException {
		ServerContainer serverContainer = actionContext.getObject("serverContainer");
		serverContainer.addEndpoint(WebsocketJavax.class);
	}
}
