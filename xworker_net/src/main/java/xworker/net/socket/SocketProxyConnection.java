package xworker.net.socket;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import xworker.lang.executor.Executor;

public class SocketProxyConnection implements Runnable{
	private static final String TAG = SocketProxyConnection.class.getName();
	
	Socket socket;
	String proxyHost;
	int proxyPort;
	boolean closed = false;
	Thing thing;
	ActionContext actionContext;
	FileOutputStream inOut = null;
	FileOutputStream outOut = null;
	long byteFromClient = 0;
	long byteFromTarget = 0;
	
	public SocketProxyConnection(Thing thing, ActionContext actionContext, Socket socket, String proxyHost, int proxyPort){
		this.socket = socket;
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	public void close(){
		this.closed = true;
	}

	@Override
	public void run() {
		final String target = proxyHost + ":" + proxyPort;
		try{			
			final Socket remoteSocket = new Socket(proxyHost, proxyPort);
			final InputStream remoteIn = remoteSocket.getInputStream();
			final OutputStream remoteOut = remoteSocket.getOutputStream();
			final InputStream in = socket.getInputStream();
			final OutputStream out = socket.getOutputStream();
			Executor.info(TAG, "Target connected: " + proxyHost + ":" + proxyPort + ", client: " + socket.toString());
			
			final boolean save = thing.doAction("isSaveData", actionContext);
			if(save){
				String path = thing.doAction("getSaveDataPath", actionContext);
				if(path == null || "".equals(path)){
					path = World.getInstance().getPath() + "/work/socketproxy/" + thing.getMetadata().getName() + "/";
				}
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
				Date date = new Date();
				path = path + sf.format(date);
				sf = new SimpleDateFormat("HH-mm-ss");
	
				File file = new File(path);
				if(!file.exists()){
					file.mkdirs();
				}
				inOut = new FileOutputStream(path + "/in_" + socket.hashCode() + "_" + sf.format(date) + ".dat");
				outOut = new FileOutputStream(path + "/out_" + socket.hashCode() + "_" + sf.format(date) + ".dat");
			}			
			
			//从远程读取字节的线程
			new Thread(() -> {
				try{
					byte[] bytes = new byte[1024 * 16];
					int length = -1;
					while((length = remoteIn.read(bytes)) != -1){
						out.write(bytes, 0, length);
						out.flush();

						if(save && inOut != null){
							inOut.write(bytes, 0, length);
							inOut.flush();
						}
						if(closed){
							break;
						}

						 byteFromTarget += length;
					}
				}catch(Exception e){
					//logger.error(hostInfo + ": read remote bytes error", e);
				}finally{
					if(inOut!= null){
						try {
							inOut.close();
						} catch (IOException e) {
							Executor.error(TAG, "close in data error", e);
						}
					}

					try{
						//试图终端客户端的连接
						if(socket.isConnected()){
							socket.close();
						}
					}catch(Exception e){
					}
					//logger.info("从" + hostInfo + "读取数据总数：" + byteFromTarget);
				}

				closed = true;
			}).start();
					
			try{
				while(socket.isConnected()){
					byte[] bytes = new byte[1024 * 16];
					int length = -1;
					try {
						while((length = in.read(bytes)) != -1){
							if(closed){
								break;
							}							
							remoteOut.write(bytes, 0, length);
							remoteOut.flush();
							
							if(save && outOut != null){
								outOut.write(bytes, 0, length);
								outOut.flush();
							}
							
							byteFromClient += length;
						}				
										
					} catch (IOException e) {
						//logger.error(socket.toString()  + ": read bytes error", e);
					}
					
					closed = true;
					break;
				}
			}finally{
				try {
					socket.close();
				} catch (IOException e) {
					Executor.error(TAG, "close socket error", e);
				}
				try {
					remoteSocket.close();
				} catch (IOException e) {
					Executor.error(TAG, "close remote socket error", e);
				}
				try {
					if(outOut != null){
						outOut.close();
					}
				} catch (IOException e) {
					Executor.error(TAG, "close data outfile error", e);
				}
				
				String  client = socket.toString();
				//logger.info("从" + socket.toString() + "读取数据总数：" + byteFromClient);
				String info = "The proxy from " + client + " to " + target + " has stopped, " +
						"Client send bytes count: " + byteFromClient + ", Target send bytes count: " + byteFromTarget;
				Executor.info(TAG, info);
			}
		}catch(Exception e){
			Executor.error(TAG, "Connect to  " + proxyHost + ":" + proxyPort + " failurem, client: " + socket.toString());
		}
	}

}
