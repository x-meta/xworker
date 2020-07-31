package xworker.http;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/socketx/*")
public class WebsocketJavax {
	@OnOpen
	public void onOpen(Session session, EndpointConfig config) {
		System.out.println("onOpen");
	}

	@OnError
	public void onError(Session session, Throwable thr) {
		System.out.println("onError");
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		System.out.println("onclose");
		
	}

	@OnMessage
	public void processTextMessage(String message, Session session) {
		System.out.println("Greeting received:" + message);
	}

	@OnMessage
	public void processBinaryMessage(byte[] b, boolean last, Session session) {
		System.out.println("收到部分二进制数据");
	}
	
	@OnMessage
	public void processBinaryMessage(byte[] b, Session session) {
		System.out.println("收到全部二进制数据");
	}

}
