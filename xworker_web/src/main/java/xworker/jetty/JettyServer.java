package xworker.jetty;

import java.io.File;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
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
		
		server =  null;
		if(UtilData.isTrue(self.doAction("isSsl", actionContext))) {
			server = new Server();

	        // create factory for ssl
	        final SslContextFactory sslContextFactory = new SslContextFactory();

	        // Set keystore file path
	        File file = self.doAction("getKeyStore", actionContext);
	        sslContextFactory.setKeyStorePath(file.getAbsolutePath());

	        // Set keystorepassword
	        String keyStorePassword = self.doAction("getKeyStorePassword", actionContext);
	        sslContextFactory.setKeyStorePassword(keyStorePassword);
	        
	        ServerConnector httpConnector = new ServerConnector(server);
	        httpConnector.setPort(port);
	        
	        // create connector for https
	        ServerConnector httpsConnector = new ServerConnector(server, sslContextFactory);
	        httpsConnector.setPort(443);
	        
	        // Set connector
	        server.setConnectors(new Connector[] { httpConnector, httpsConnector });
		}else {
			server = new Server(port);
		}
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
	
	/**
	 * 使用XWorker下的KeyStore创建SSL的Connector。KeyStore路径是/config/jetty/jettykeystore.jks。
	 * 
	 * @param server
	 * @param sslPort
	 * @return
	 */
	public static ServerConnector createXWorkerSSLConnector(Server server, int sslPort) {
		World world = World.getInstance();
		
		// create factory for ssl
        final SslContextFactory sslContextFactory = new SslContextFactory();

        // Set keystore file path
        sslContextFactory.setKeyStorePath(world.getPath() + "/config/jetty/jettykeystore.jks");

        // Set keystorepassword
        String keyStorePassword = "xworker";
        sslContextFactory.setKeyStorePassword(keyStorePassword);
        
        // create connector for https
        ServerConnector httpsConnector = new ServerConnector(server, sslContextFactory);
        httpsConnector.setPort(sslPort);
        
        return httpsConnector;
	}
	
	/**
	 * 创建并返回一个Connector。
	 * 
	 * @param server
	 * @param port
	 * @return
	 */
	public static ServerConnector createConnector(Server server, int port) {
		ServerConnector httpConnector = new ServerConnector(server);
        httpConnector.setPort(port);
        return httpConnector;
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
