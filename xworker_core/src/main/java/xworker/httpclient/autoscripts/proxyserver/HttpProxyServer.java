package xworker.httpclient.autoscripts.proxyserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class HttpProxyServer implements Runnable{	
	private static Logger logger = LoggerFactory.getLogger(HttpProxyServer.class);
	
	static Map<String, HttpProxyServer> servers = new HashMap<String, HttpProxyServer>();
	
	ServerSocket ss;
	boolean stop = false;
	boolean stoped = false;
	
	public HttpProxyServer(int port) throws IOException{
		ss = new ServerSocket(port);
		logger.info("HttpProxyServer listened at prot :" + port);
	}
	
	public void run(){		
		try {
			while(!stop){
				Socket socket = ss.accept();
				
				new LocalThread(socket).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		stoped = true;
	}
	
	public void stop() throws IOException{
		if(!stoped){
			ss.close();
		}
	}
	
	public static void stop(Thing thing) throws IOException{
		HttpProxyServer server = servers.get(thing.getMetadata().getPath());
		if(server != null){
			server.stop();
			
			servers.remove(thing.getMetadata().getPath());
		}
	}
	
	public static void run(ActionContext actionContext) throws IOException{
		Thing self = (Thing) actionContext.get("self");
		
		HttpProxyServer server = servers.get(self.getMetadata().getPath());
		if(server != null && server.stoped == false){
			return;
		}
				
		int port = self.getInt("port");
		server = new HttpProxyServer(port);
		new Thread(server).start();
		
		servers.put(self.getMetadata().getPath(), server);
	}
}
