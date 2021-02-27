package xworker.io.netty.handlers.http;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.AttributeKey;

public class HttpSessionManager {	
	private static Map<String, HttpSession> sessions = new HashMap<String, HttpSession>();
	
	public static HttpSession getHttpSession(ChannelHandlerContext ctx, FullHttpRequest request, RequestBean requestBean) {
		String sessionId = requestBean.getCookieValue(HttpSession.SESSIONID);
		HttpSession httpSession = null;
		if(sessionId != null) {
			httpSession = sessions.get(sessionId);
		}
		
		if(httpSession != null && httpSession.isExpired()) {
			httpSession = null;
		}
		
		if(httpSession == null) {
			sessionId = UUID.randomUUID().toString();
			httpSession = new HttpSession(sessionId);
			sessions.put(sessionId, httpSession);
		}
		
		httpSession.active();
		
		return httpSession;		
	}
}
