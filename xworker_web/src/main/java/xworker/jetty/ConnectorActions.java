package xworker.jetty;

import java.io.File;
import java.net.Socket;
import java.nio.ByteBuffer;

import org.eclipse.jetty.io.NetworkTrafficListener;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.NetworkTrafficServerConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ConnectorActions {
	public static Object createLocalConnector(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Server server = actionContext.getObject("server");
		LocalConnector connector = new LocalConnector(server);
		
		actionContext.g().put(self.getMetadata().getName(), connector);
		
		server.addConnector(connector);
		return connector;
	}
	
	public static Object createServerConnector(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Server server = actionContext.getObject("server");
		ServerConnector connector =  null;
		Boolean ssl = self.doAction("isSsl", actionContext);
		if(ssl != null && ssl){
			// create factory for ssl
	        final SslContextFactory sslContextFactory = new SslContextFactory();

	        // Set keystore file path
	        File file = self.doAction("getKeyStore", actionContext);
	        sslContextFactory.setKeyStorePath(file.getAbsolutePath());

	        // Set keystorepassword
	        String keyStorePassword = self.doAction("getKeyStorePassword", actionContext);
	        sslContextFactory.setKeyStorePassword(keyStorePassword);
	        
	        connector = new ServerConnector(server, sslContextFactory);
		} else {
			connector = new ServerConnector(server);
		}
		
		String host = self.doAction("getHost", actionContext);
		if(host != null){
			connector.setHost(host);
		}

		int port = self.doAction("getPort", actionContext);
		if(port > 0){
			connector.setPort(port);
		}

		long idleTimeout = self.doAction("getIdleTimeout", actionContext);
		if(connector != null){
			connector.setIdleTimeout(idleTimeout);
		}

		String defaultProtocol = self.doAction("getDefaultProtocol", actionContext);
		if(defaultProtocol != null){
			connector.setDefaultProtocol(defaultProtocol);
		}

		int acceptorPriorityDelta = self.doAction("getAcceptorPriorityDelta", actionContext);
		if(acceptorPriorityDelta > -1){
			connector.setAcceptorPriorityDelta(acceptorPriorityDelta);
		}

		int acceptQueueSize = self.doAction("getAcceptQueueSize", actionContext);
		if(acceptQueueSize > 0){
			connector.setAcceptQueueSize(acceptQueueSize);
		}

		Boolean inheritChannel = self.doAction("isInheritChannel", actionContext);
		if(inheritChannel != null){
			connector.setInheritChannel(inheritChannel);
		}

		Boolean reuseAddress = self.doAction("isReuseAddress", actionContext);
		if(reuseAddress != null){
			connector.setReuseAddress(reuseAddress);
		}

		actionContext.g().put(self.getMetadata().getName(), connector);
		
		server.addConnector(connector);
		return connector;
	}
	
	public static Object createNetworkTrafficServerConnector(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		Server server = actionContext.getObject("server");
		NetworkTrafficServerConnector connector =  null;
		Boolean ssl = self.doAction("isSsl", actionContext);
		if(ssl != null && ssl){
			// create factory for ssl
	        final SslContextFactory sslContextFactory = new SslContextFactory();

	        // Set keystore file path
	        File file = self.doAction("getKeyStore", actionContext);
	        sslContextFactory.setKeyStorePath(file.getAbsolutePath());

	        // Set keystorepassword
	        String keyStorePassword = self.doAction("getKeyStorePassword", actionContext);
	        sslContextFactory.setKeyStorePassword(keyStorePassword);
	        
	        connector = new NetworkTrafficServerConnector(server, sslContextFactory);
		} else {
			connector = new NetworkTrafficServerConnector(server);
		}
		
		connector.addNetworkTrafficListener(new ThingNetworkTrafficListener(self, actionContext));
		String host = self.doAction("getHost", actionContext);
		if(host != null){
			connector.setHost(host);
		}

		int port = self.doAction("getPort", actionContext);
		if(port > 0){
			connector.setPort(port);
		}

		long idleTimeout = self.doAction("getIdleTimeout", actionContext);
		if(connector != null){
			connector.setIdleTimeout(idleTimeout);
		}

		String defaultProtocol = self.doAction("getDefaultProtocol", actionContext);
		if(defaultProtocol != null){
			connector.setDefaultProtocol(defaultProtocol);
		}

		int acceptorPriorityDelta = self.doAction("getAcceptorPriorityDelta", actionContext);
		if(acceptorPriorityDelta > -1){
			connector.setAcceptorPriorityDelta(acceptorPriorityDelta);
		}

		int acceptQueueSize = self.doAction("getAcceptQueueSize", actionContext);
		if(acceptQueueSize > 0){
			connector.setAcceptQueueSize(acceptQueueSize);
		}

		Boolean inheritChannel = self.doAction("isInheritChannel", actionContext);
		if(inheritChannel != null){
			connector.setInheritChannel(inheritChannel);
		}

		Boolean reuseAddress = self.doAction("isReuseAddress", actionContext);
		if(reuseAddress != null){
			connector.setReuseAddress(reuseAddress);
		}

		actionContext.g().put(self.getMetadata().getName(), connector);
		
		server.addConnector(connector);
		return connector;
	}
	
}
