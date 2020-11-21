package xworker.io.netty.handlers.http.websocketx;

import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
	Thing thing;
	ActionContext actionContext;
	
	public WebSocketFrameHandler(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
    	Bindings bindings = actionContext.push();
    	bindings.put("frame", frame);
    	bindings.put("ctx", ctx);
    	
    	try {
	    	thing.doAction("onFrame", actionContext);
	    	
	        if (frame instanceof TextWebSocketFrame) {
	            thing.doAction("onText", actionContext);
	        }else if (frame instanceof BinaryWebSocketFrame) {
	            thing.doAction("onBinary", actionContext);
	        }else if (frame instanceof CloseWebSocketFrame) {
	            thing.doAction("onClose", actionContext);
	        }else if (frame instanceof ContinuationWebSocketFrame) {
	            thing.doAction("onContinuation", actionContext);
	        }else if (frame instanceof PingWebSocketFrame) {
	            thing.doAction("onPing", actionContext);
	        }else if (frame instanceof PongWebSocketFrame) {
	            thing.doAction("Pong", actionContext);
	        }else {
	            String message = "unsupported frame type: " + frame.getClass().getName();
	            throw new UnsupportedOperationException(message);
	        }
    	}finally {
    		actionContext.pop();
    	}
    }

    public static WebSocketFrameHandler create(ActionContext actionContext) {
    	Thing self = actionContext.getObject("self");
    	
    	return new WebSocketFrameHandler(self, actionContext);
    }
}
