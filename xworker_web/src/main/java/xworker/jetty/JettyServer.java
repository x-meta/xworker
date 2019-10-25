package xworker.jetty;

import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import xworker.cache.object.ObjectContext;
import xworker.cache.object.ObjectManager;

public class JettyServer {
	private static Logger logger = LoggerFactory.getLogger(JettyServer.class);
	
	public static Server start(ActionContext actionContext) throws Exception {
		Thing self = actionContext.getObject("self");
		
		Server server = getServer(self);
		if(server != null) {
			if(server.isStarted() || server.isStarting() || server.isRunning()) {
				logger.warn("JettyServer alreay started, path=" + self.getMetadata().getPath());
				//正在运行或启动中
				return server;
			}
			
			server.stop();
		}
		
		int port = self.doAction("getPort", actionContext);
		boolean stopAtShutDown = UtilData.isTrue(self.doAction("isStopAtShutdown", actionContext));
		
		server = new Server(port);
		putServer(self, server, actionContext);
		
		server.setStopAtShutdown(stopAtShutDown);
		//创建Handler
		actionContext.peek().put("server", server);
		for(Thing handlers : self.getChilds("Handlers")) {
			for(Thing handler : handlers.getChilds()) {
				handler.doAction("create", actionContext);
			}
		}
		
		//执行初始化
		self.doAction("init", actionContext);
		
		//启动Jetty服务器
		server.start();
		return server;
	}
	
	public static void stop(ActionContext actionContext) throws Exception {
		Thing self = actionContext.getObject("self");
		
		Server server = getServer(self);
		if(server != null) {
			server.stop();
		}
	}
	
	private static Server getServer(Thing thing){
		ObjectContext context = ObjectManager.get("xworker.jetty.Jetty", thing);
		if(context != null){
			return (Server) context.getObject();
		}else{
			return null;
		}		
	}
	
	private static void putServer(Thing thing, Server server, ActionContext actionContext){
		ObjectManager.put("xworker.jetty.Jetty", thing, server, actionContext);
	}
}
