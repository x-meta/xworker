package xworker.net.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class SocketProxy  implements Runnable{
	private static Logger logger = LoggerFactory.getLogger(SocketProxy.class);
	private static final String key = "__SocketProxy_instance__";
	int port;
	String proxyHost;
	int proxyPort;
	boolean closed = false;
	Thing thing;
	ActionContext actionContext;
	ServerSocket ss = null;
	
	public SocketProxy(Thing thing, ActionContext actionContext, int port, String proxyHost, int proxyPort){
		this.port = port;
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	public static SocketProxy start(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		int port = (Integer) self.doAction("getPort", actionContext);
		String proxyHost = (String) self.doAction("getProxyHost", actionContext);
		int proxyPort = (Integer) self.doAction("getProxyPort", actionContext);
		
		//每次只有一个实例
		SocketProxy sp = (SocketProxy) self.getData(key);
		if(sp != null){
			if(sp.port == port && sp.proxyHost.equals(proxyHost) && sp.proxyPort == proxyPort && !sp.isClosed()){
				return sp;
			}else{
				sp.setClosed(true);
			}
		}
		
		//创建和启动实例
		sp = new SocketProxy(self, actionContext, port, proxyHost, proxyPort);
		self.setData(key, sp);
		new Thread(sp, self.getMetadata().getLabel()).start();
		
		return sp;
		
	}
	
	public static void stop(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		SocketProxy sp = (SocketProxy) self.getData(key);
		if(sp != null){
			sp.setClosed(true);
		}
	}
	
	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
		
		if(ss != null && !ss.isClosed()){
			try {
				ss.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		
		try{
			logger.info(thing.getMetadata().getLabel() + " start at " + port);
			ss = new ServerSocket(port);			
			logger.info(thing.getMetadata().getLabel() + " started");
			
			while(true){
				if(closed){
					break;
				}
				Socket socket = ss.accept();
				logger.info(thing.getMetadata().getLabel() + "连接 " + socket);
				new Thread(new SocketProxyConnection(thing, actionContext, socket, proxyHost, proxyPort)).start();
			}
			
			logger.info(thing.getMetadata().getLabel()  + " stoped");
		}catch(Exception e){
			logger.info(thing.getMetadata().getLabel()  + " closed");
		}finally{
			try{
				if(ss != null){
					ss.close();
				}
			}catch(Exception e){				
			}
		}
	}
}
