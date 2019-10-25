package xworker.http;

import java.net.URI;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketFrame;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.extensions.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

@WebSocket
public class WebSocketHandler {
	private static Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);
	
	public void doAction(Session session, String actionName, Object ... params) {
		try {
			URI uri = session.getUpgradeRequest().getRequestURI();
			String path = uri.getPath();
			int index = path.indexOf("/", 1);
			path = path.substring(index + 1, path.length());
			path = path.replace('/', '.');
			
			Thing thing = World.getInstance().getThing(path);
			if(thing == null) {
				logger.warn("Thing not exists so ingore websocket event, path=" + path + ", \nshoud set thing path in url, "
						+ "for example contextPath is /socket/ and thingPath is test.TestWebSocket then url is /socket/test/TestWebSocket");
			}else {
				ActionContext actionContext = new ActionContext();
				actionContext.put("session", session);
				actionContext.put("httpSession", session.getUpgradeRequest().getSession());
				
				thing.doAction(actionName, actionContext, params);
			}
		}catch(Throwable t) {
			logger.error("Handle event error", t);
		}
	}
	
	@OnWebSocketMessage
	public void onTextMessage(Session session, String text) {
		doAction(session, "onTextMessage", "session", session, "text", text);
	}
	
	public void onBinaryMessage(Session session, byte[] buf, int offset, int length) {
		doAction(session, "onBinaryMessage", "session", session, "buf", buf, "offset", offset, "length", length);
	}
	
	@OnWebSocketConnect
	public void onWebSocketConnect(Session session) {
		doAction(session, "onWebSocketConnect", "session", session);
	}
	
	@OnWebSocketClose
	public void onWebSocketClose(Session session, int closeCode, String closeReason ) {
		doAction(session, "onWebSocketClose", "session", session, "closeCode", closeCode, "closeReason", closeReason);
	}
	
	@OnWebSocketError
	public void onWebSocketError(Session session, Throwable cause) {
		doAction(session, "onWebSocketError", "session", session, "cause", cause);
	}
	
	@OnWebSocketFrame
	public void onWebSocketFrame(Session session, Frame frame) {
		doAction(session, "onWebSocketFrame", "session", session, "frame", frame);
	}
}
