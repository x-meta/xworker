package xworker.jetty.handlers;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.UtilData;

public class WebAppContextActions {
	public static void create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Server server = actionContext.getObject("server");
		String webApp = self.doAction("getWebApp", actionContext);
		String contextPath = self.doAction("getContextPath", actionContext);
		String resourceBase = self.doAction("getResourceBase", actionContext);
		String[] welcomeFiles = self.doAction("getWelcomeFiles", actionContext);
		
		WebAppContext context = new WebAppContext(webApp, contextPath);     	  
	    
		if(resourceBase != null && !"".equals(resourceBase)) { 
			context.setResourceBase(resourceBase);
		}
	    
		if(welcomeFiles != null) {
	        context.setWelcomeFiles(welcomeFiles);
		}
				
		context.setClassLoader(World.getInstance().getClassLoader());
	    context.setAttribute("org.eclipse.jetty.servlet.Default.dirAllowed", UtilData.isTrue(
	    		self.doAction("isDirAllowed", actionContext)));
	    
	    //SWT rwt会出现文件明明存在，但404问题，加下面的语句可以解决
	    context.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
	    
	    //执行初始化
	    self.doAction("init", actionContext, "context", context);
	    
	    //添加到Server
	    server.setHandler(context);
	}
}
