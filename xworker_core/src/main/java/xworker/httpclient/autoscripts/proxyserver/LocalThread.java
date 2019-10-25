package xworker.httpclient.autoscripts.proxyserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.Thing;

public class LocalThread extends Thread{
	private static Logger logger = LoggerFactory.getLogger(LocalThread.class);
	
	Socket socket;
	InputStream in;
	OutputStream out;
	ByteArrayOutputStream bout = new ByteArrayOutputStream();
	String url;
	
	public LocalThread(Socket socket) throws IOException{
		this.socket = socket;
		this.in= socket.getInputStream();
		this.out = socket.getOutputStream();
	}
	
	public void run(){
		logger.info("HttpProxyServer: start session " + socket);
		Thing requests = new Thing("xworker.httpclient.HttpAutoScript");
		Thing request = new Thing("xworker.httpclient.HttpAutoScript/@Rquest");
		requests.addChild(request);
		try{
			OutputStream rout = null;
			byte[] bytes = new byte[2048];
			int length = -1;
			while((length = in.read(bytes)) != -1){
				bout.write(bytes, 0, length);
				if(rout != null){
					bout.write(bytes, 0, length);
					rout.write(bytes, 0, length);
				}else{
					byte[] bbytes = bout.toByteArray();
					String line = new String(bbytes);
					int index = line.indexOf("\n");
					if(index != -1){
						line = line.substring(0, index);
						String url = line.split("[ ]")[1];
						System.out.println(url);
						if(!url.startsWith("http")){
							url = "http://" + url;
						}
						URL u = new URL(url);
						String host = u.getHost();
						int port = u.getPort();
						
						RemoteThread th = new RemoteThread(host, port, out);
						th.start();
						rout = th.getOutputStream();
						rout.write(bbytes);
						
						request.put("host", host);
						request.put("port", port);
					}
				}
			}
			
			request.put("content", bout.toString());
			requests.saveAs("_local", "_local.Request");
		}catch(Exception e){
			logger.error("Local Thread error", e);
		}
	}
}
