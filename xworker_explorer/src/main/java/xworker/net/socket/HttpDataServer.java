package xworker.net.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;


/**
 * 模拟的HttpServer。
 * 
 * @author zyx
 *
 */
public class HttpDataServer implements Runnable{
	Map<String, byte[]> datas = null;
	ServerSocket ss = null;
	
	public HttpDataServer(int port, Map<String, byte[]> datas) throws IOException{
		ss = new ServerSocket(port);
		this.datas = datas;
	}
	
	public void run(){
		try{
			while(true){
				Socket socket = ss.accept();
				
				InputStream in = socket.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String line = br.readLine();
				String url = line.split("[ ]")[1];
				
				byte[] bs = datas.get(url);
				if(bs != null){
					OutputStream out = socket.getOutputStream();
					out.write(bs);
					out.close();
					out.flush();
				}
				
				try{
					//停1秒，然后关闭socket
					Thread.sleep(1000);
					socket.close();
				}catch(Exception e){
					e.printStackTrace();
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
