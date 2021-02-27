package xworker.io.netty.handlers.http.websocketx;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;

public class WebSocketHandlers {
	public static WebSocketServerCompressionHandler createWebSocketServerCompressionHandler(ActionContext actionContext) {
		return new WebSocketServerCompressionHandler();
	}
	
	public static WebSocketClientCompressionHandler createWebSocketClientCompressionHandler(ActionContext actionContext) {
		return WebSocketClientCompressionHandler.INSTANCE;
	}
	
	public static WebSocketServerProtocolHandler createWebSocketServerProtocolHandler(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		String websocketPath = self.doAction("getWebsocketPath", actionContext);
		String subprotocols = self.doAction("getSubprotocols", actionContext);
		Boolean allowExtensions = self.doAction("isAllowExtensions", actionContext);
		int maxFrameSize = self.doAction("getMaxFrameSize", actionContext);
		Boolean allowMaskMismatch = self.doAction("isAllowMaskMismatch", actionContext);
		Boolean checkStartsWith = self.doAction("isCheckStartsWith", actionContext);
		Boolean dropPongFrames = self.doAction("isDropPongFrames", actionContext);

		return new WebSocketServerProtocolHandler(websocketPath, subprotocols, allowExtensions, maxFrameSize, allowMaskMismatch,  checkStartsWith, dropPongFrames);
	}
}
