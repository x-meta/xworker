package xworker.io.tools.echoSocketServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoSocketServer extends Thread{
	ServerSocket serverSocket;
	boolean print;
	boolean echoToClient;
	
	public EchoSocketServer(int port, boolean print, boolean echoToClient) throws IOException{
		serverSocket = new ServerSocket(port);
		this.print = print;
		this.echoToClient = echoToClient;
	}
	
	public void stopServer(){
		if(serverSocket != null){
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void run(){
		Socket socket = null;
		try {
			while((socket = serverSocket.accept()) != null){
				EchoScoketThread th = new EchoScoketThread(socket, print, echoToClient);
				th.start();
			}
		} catch (IOException e) {
			if(!serverSocket.isClosed()){
				System.out.println("EchoScoketServer error: " + serverSocket);
				e.printStackTrace();
			}else{
				System.out.println("SocketServer closed: " + serverSocket);
			}
		}finally{
			if(serverSocket != null){
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
