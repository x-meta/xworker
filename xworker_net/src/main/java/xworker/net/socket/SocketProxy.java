package xworker.net.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import xworker.lang.executor.Executor;

public class SocketProxy  implements Runnable{
	private static final String TAG = SocketProxy.class.getName();
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
		int port = self.doAction("getPort", actionContext);
		String proxyHost = self.doAction("getProxyHost", actionContext);
		int proxyPort = self.doAction("getProxyPort", actionContext);
		
		//每次只有一个实例
		SocketProxy sp = self.getData(key);
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
		SocketProxy sp = self.getData(key);
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
			Executor.info(TAG, thing.getMetadata().getLabel() + " start at " + port);
			ss = new ServerSocket(port);
			Executor.info(TAG, thing.getMetadata().getLabel() + " started");

			while (!closed) {
				Socket socket = ss.accept();
				Executor.info(TAG, thing.getMetadata().getLabel() + " connect " + socket);
				new Thread(new SocketProxyConnection(thing, actionContext, socket, proxyHost, proxyPort)).start();
			}

			Executor.info(TAG, thing.getMetadata().getLabel()  + " stoped");
		}catch(Exception e){
			Executor.info(TAG, thing.getMetadata().getLabel()  + " closed");
		}finally{
			try{
				if(ss != null){
					ss.close();
				}
			}catch(Exception ignored){
			}
		}
	}
}
