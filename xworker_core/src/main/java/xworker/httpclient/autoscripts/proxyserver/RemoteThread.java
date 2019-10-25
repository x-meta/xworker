package xworker.httpclient.autoscripts.proxyserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteThread extends Thread{
	private static Logger logger = LoggerFactory.getLogger(RemoteThread.class);
	
	Socket socket;
	String host;
	int port;
	InputStream in;
	OutputStream out;
	OutputStream localOut;
	
	public RemoteThread(String host, int port, OutputStream localOut) throws UnknownHostException, IOException{
		socket = new Socket(host, port == -1 ? 80 : port);
		in = socket.getInputStream();
		out = socket.getOutputStream();
		this.localOut = localOut;
	}
	
	public OutputStream getOutputStream(){
		return out;
	}
	
	public void run(){
		logger.info("HttpProxyServer: connect remote: " + socket);
		try{
			byte[] bytes = new byte[2048];
			int length = -1;
			while((length = in.read(bytes)) != -1){
				localOut.write(bytes, 0,  length);
			}
		}catch(Exception e){
			logger.error("Remote Thread error", e);
		}
	}
}
