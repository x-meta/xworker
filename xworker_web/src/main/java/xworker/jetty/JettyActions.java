package xworker.jetty;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.LocalConnector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.nio.BlockingChannelConnector;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.security.SslSocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import xworker.cache.object.ThingObject;
import xworker.cache.object.ObjectManager;

public class JettyActions {
	private static Logger logger = LoggerFactory.getLogger(JettyActions.class);
		
	public static void stop(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		//World world = World.getInstance();
		Server server = getServer(self);
		if(server != null && !(server.isStopped() || server.isFailed())){
			try {
				server.stop();
			} catch (Exception e) {
				logger.info("Stop jetty error, path=" + self.getMetadata().getPath(), e);
			}
		}
		
		if(server != null){
			ObjectManager.remove("xworker.jetty.Jetty", self);
		}
	}

	private static Server getServer(Thing thing){
		ThingObject context = ObjectManager.get("xworker.jetty.Jetty", thing);
		if(context != null){
			return (Server) context.getObject();
		}else{
			return null;
		}		
	}
	
	private static void putServer(Thing thing, Server server, ActionContext actionContext){
		ObjectManager.put("xworker.jetty.Jetty", thing, server, actionContext);
	}
	
	public static Server create(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		//World world = World.getInstance();
		
		Server server = getServer(self);
		if(server == null || server.isStopped() || server.isFailed()){
			server = new Server();
			Bindings bindings = actionContext.push(null);
			bindings.put("server", server);
			try{
				for(Thing child : self.getChilds()){
					child.doAction("create", actionContext);
				}
			}finally{
				actionContext.pop();
			}
			
			final Server aserver = server;
			new Thread(new Runnable(){
				public void run(){
					try {
						aserver.start();
						aserver.join();
					} catch (Exception e) {
						logger.error("Start jetty server error", e);
					}
					
				}
			}).start();
			
			putServer(self, server, actionContext);
		}else{
			logger.info("jetty server is already exists and running, path=" + self.getMetadata().getPath());
		}
		
		return server;
	}

	
	public static void createWebAppContext(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		String contextPath = (String) self.doAction("getContextPath", actionContext);
		String webApp = (String) self.doAction("getWebApp", actionContext);
		
		Server server = (Server) actionContext.get("server");
		WebAppContext context = new WebAppContext(webApp, contextPath);
		server.addHandler(context);
	}
	
	public static void createConnector(ActionContext actionContext){
		Thing self = (Thing) actionContext.get("self");
		Server server = (Server) actionContext.get("server");
		Connector connector = null;
		String type = self.getString("type");
		if("Ajp13SocketConnector".equals(type)){
			//connector = new Ajp13SocketConnector(); 
		}else if("BlockingChannelConnector".equals(type)){
			connector = new BlockingChannelConnector();
		}else if("InheritedChannelConnector".equals(type)){
			//connector = new InheritedChannelConnector();
		}else if("LocalConnector".equals(type)){
			connector = new LocalConnector();
		}else if("SelectChannelConnector".equals(type)){
			connector = new SelectChannelConnector();
		}else if("SocketConnector".equals(type)){
			connector = new SocketConnector();
		}else if("SslSelectChannelConnector".equals(type)){
			//connector = new SslSelectChannelConnector();
		}else if("SslSocketConnector".equals(type)){
			connector = new SslSocketConnector();
		}
		
		if(connector != null){
			String host = (String) self.doAction("getHost", actionContext);
			int port = (Integer) self.doAction("getPort", actionContext);
			if(host != null){
				connector.setHost(host);				
			}
			
			connector.setPort(port);
			
			server.addConnector(connector);
		}
	}
}
