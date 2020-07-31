package xworker.jetty;

import java.io.File;

import org.eclipse.jetty.server.AsyncNCSARequestLog;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.Slf4jRequestLog;
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
		
		server =  new Server();
		if(UtilData.isTrue(self.doAction("isSsl", actionContext))) {
	        // create factory for ssl
	        final SslContextFactory sslContextFactory = new SslContextFactory();

	        // Set keystore file path
	        File file = self.doAction("getKeyStore", actionContext);
	        sslContextFactory.setKeyStorePath(file.getAbsolutePath());

	        // Set keystorepassword
	        String keyStorePassword = self.doAction("getKeyStorePassword", actionContext);
	        sslContextFactory.setKeyStorePassword(keyStorePassword);
	        
	        // create connector for https
	        ServerConnector httpsConnector = new ServerConnector(server, sslContextFactory);
	        int sslPort = self.doAction("getSslPort", actionContext);
	        if(sslPort <= 0) {
	        	sslPort = 443;
	        }
	        httpsConnector.setPort(443);
	        
	        // Set connector
	        server.addConnector(httpsConnector);
		}
		
		if(port > 0) {
	        ServerConnector httpConnector = new ServerConnector(server);
	        httpConnector.setPort(port);
	        server.addConnector(httpConnector);
		}

		
		server.setDumpAfterStart(self.doAction("isDumpAfterStart", actionContext));
		server.setDumpBeforeStop(self.doAction("isDumpBeforeStop", actionContext));
		
		putServer(self, server, actionContext);
		
		server.setStopAtShutdown(stopAtShutDown);
		//创建Handler
		actionContext.peek().put("server", server);		
		for(Thing handlers : self.getChilds("Handlers")) {
			boolean setted = false;
			for(Thing handler : handlers.getChilds()) {
				Handler h = handler.doAction("create", actionContext);
				if(h != null) {
					server.setHandler(h);
					setted = true;
					break;
				}
			}
			
			if(setted) {
				break;
			}
		}
		
		
		//创建Connector
		for(Thing connectors : self.getChilds("Connectors")) {
			for(Thing connector : connectors.getChilds()) {
				connector.doAction("create", actionContext);
			}
		}
		
		for(Thing logs : self.getChilds("RequestLogs")) {
			boolean ok = false;
			for(Thing log : logs.getChilds()) {
				RequestLog l = log.doAction("create", actionContext);
				if(l != null) {
					server.setRequestLog(l);
					ok = true;
					break;
				}
			}
			
			if(ok) {
				break;
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
	
	
	public static NCSARequestLog createNCSARequestLog(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		NCSARequestLog log = new NCSARequestLog();
		Boolean isAppend = self.doAction("isAppend", actionContext);
		if(isAppend != null) {
			log.setAppend(isAppend);
		}
		
		log.setFilename(self.doAction("getFilename", actionContext));
		
		String dateFormat = self.doAction("getLogFileDateFormat", actionContext);
		if(dateFormat != null && !"".equals(dateFormat)) {
			log.setFilenameDateFormat(dateFormat);
		}
		
		int retainDays = self.doAction("getRetainDays", actionContext);
		if(retainDays > 0) {
			log.setRetainDays(retainDays);
		}
		
		AbstractNCSARequestLogThing.init(log, self, actionContext);
		return log;
	}
	
	public static AsyncNCSARequestLog ceateAsyncNCSARequestLog(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		AsyncNCSARequestLog log = new AsyncNCSARequestLog();
		Boolean isAppend = self.doAction("isAppend", actionContext);
		if(isAppend != null) {
			log.setAppend(isAppend);
		}
		
		log.setFilename(self.doAction("getFilename", actionContext));
		
		String dateFormat = self.doAction("getLogFileDateFormat", actionContext);
		if(dateFormat != null && !"".equals(dateFormat)) {
			log.setFilenameDateFormat(dateFormat);
		}
		
		int retainDays = self.doAction("getRetainDays", actionContext);
		if(retainDays > 0) {
			log.setRetainDays(retainDays);
		}
		
		AbstractNCSARequestLogThing.init(log, self, actionContext);
		return log;
	}
	
	public static Slf4jRequestLog ceateSlf4jRequestLog(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Slf4jRequestLog log = new Slf4jRequestLog();
		String loggerName = self.doAction("getLoggerName", actionContext);
		if(loggerName != null) {
			log.setLoggerName(loggerName);
		}
		AbstractNCSARequestLogThing.init(log, self, actionContext);
		
		return log;
	}
}
