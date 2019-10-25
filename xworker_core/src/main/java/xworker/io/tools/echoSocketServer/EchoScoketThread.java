package xworker.io.tools.echoSocketServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class EchoScoketThread extends Thread{
	Socket socket = null;
	boolean print;
	boolean echoToClient;
	
	public EchoScoketThread(Socket socket, boolean print, boolean echoToClient){
		this.socket = socket;
		this.print = print;
		this.echoToClient = echoToClient;
	}
	
	public void run(){
		try {
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while((line = br.readLine()) != null){
				if(print){
					System.out.println(line);
					if(echoToClient){
						out.write(line.getBytes());
					}
				}
			}
		} catch (IOException e) {
			if(!socket.isClosed()){
				System.out.println("EchoScoketThread error: " + socket);
				e.printStackTrace();
			}else{
				System.out.println("Socket closed: " + socket);
			}
		}finally{
			if(socket != null){
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
