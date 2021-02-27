package xworker.jetty;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.lang.executor.Executor;

public class SimpleHttpServer {
	private static final String TAG = SimpleHttpServer.class.getName();
	
	public static void run(ActionContext actionContext){
		try{
			World world = World.getInstance();
			Thing self = actionContext.getObject("self");
			
			//查看WEB-INFO是否存在
			File webIfo = new File("./WEB-INF");
			if(webIfo.exists() == false){
				Thing createWeb = world.getThing("xworker.tools.project.standalone.CreateProjectActions/@CreateDmlWebProject");
				createWeb.doAction("createWeb", actionContext, "dir", new File("."));
			}
			
			//XWorker之外的事物管理器增加文件改动监听
			Action monitorFile = actionContext.getObject("monitorFile");
			if(monitorFile != null) {
				monitorFile.run(actionContext);
			}
			
			//检查基本资源，编辑事物需要的基本资源
			File root = new File(".");
			String resourcesForCopy = self.getStringBlankAsNull("resourcesForCopy");
			if(resourcesForCopy != null){
				for(String res : resourcesForCopy.split("[,]")){
					checkResource(res, root);
				}
				//checkResource("js/extjs", root);
			}
			
			//把自己设置为默认的安全处理器						
			//SecurityHandler securityHandler = new WebTextSecurityHandler(self);
			//SecurityManager.setDefaultSecurityHandler(securityHandler);
			
			//启动HTTP服务器
			Server aserver = new Server(self.getInt("port"));
			/*
			server.
		    Connector connector= new SelectChannelConnector();//new SocketConnector();//new SelectChannelConnector();
		    connector.setPort(self.getInt("port"));
		    aserver.setConnectors(new Connector[]{connector});
		    */
		    //根应用
		    String contextPath = self.doAction("getContextPath", actionContext);
		    if(contextPath == null){
		    	contextPath = "/";
		    }
		    WebAppContext context = new WebAppContext(".", contextPath);     	  
		    //WebAppContext context = new WebAppContext(World.getInstance().getPath() + "/webroot/", "/");
	        context.setResourceBase(".");
		    //context.setResourceBase(World.getInstance().getPath() + "/webroot/");
	        context.setWelcomeFiles(new String[]{"index.html", "index.htm", "index.do", 
	        		"index.dml", "index.xer", "index.xer.xml", "index.xer.txt"});	        
		    context.setClassLoader(world.getClassLoader());		   
		    context.setAttribute("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
		    //SWT rwt会出现文件明明存在，但404问题，加下面的语句可以解决
		    context.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false"); 
		    
		    aserver.setHandler(context);
		    aserver.setStopAtShutdown(true);		    
		    aserver.start();		     	
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void checkResource(String path, File root) throws IOException{
	    File targetFile = new File(root, path);
	    if(!targetFile.exists()){
	        File srcFile = new File(World.getInstance().getPath() + "/webroot/" + path);
	        Executor.info(TAG, "Copping " + targetFile.getPath());
	        if(srcFile.isFile()){
	            FileUtils.copyFile(srcFile, targetFile);
	        }else{
	            FileUtils.copyDirectory(srcFile, targetFile);
	        }
	    }
	}	
}
