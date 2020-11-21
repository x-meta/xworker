package xworker.io.netty.handlers;

import java.net.InetAddress;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import xworker.lang.executor.Executor;

public class ThingChannelInboundHandler implements ChannelInboundHandler {
	private static final String TAG = ThingChannelInboundHandler.class.getName();
	
	Thing thing;
	ActionContext actionContext;
	
	public ThingChannelInboundHandler(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		thing.doAction("handlerAdded", actionContext, "ctx", ctx);
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		thing.doAction("handlerRemoved", actionContext, "ctx", ctx);
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		thing.doAction("channelRegistered", actionContext, "ctx", ctx);
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		thing.doAction("channelUnregistered", actionContext, "ctx", ctx);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		thing.doAction("channelActive", actionContext, "ctx", ctx);
		
		if(UtilData.isTrue(thing.doAction("isSslOperationComplete", actionContext))) {
			ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(
	                new GenericFutureListener<Future<Channel>>() {
	                    @Override
	                    public void operationComplete(Future<Channel> future) throws Exception {
	                    	thing.doAction("sslOperationComplete", actionContext, "ctx", ctx);
	                    }
	        });
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		thing.doAction("channelInactive", actionContext, "ctx", ctx);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		thing.doAction("channelRead", actionContext, "ctx", ctx, "msg", msg);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		thing.doAction("channelReadComplete", actionContext, "ctx", ctx);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		thing.doAction("userEventTriggered", actionContext, "ctx", ctx);
	}

	@Override
	public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
		thing.doAction("channelWritabilityChanged", actionContext, "ctx", ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		thing.doAction("exceptionCaught", actionContext, "ctx", ctx);
	}

	public static ThingChannelInboundHandler create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ThingChannelInboundHandler handler = new ThingChannelInboundHandler(self, actionContext);
		actionContext.g().put(self.getMetadata().getName(), handler);
		
		return handler;
	}
	
	public static void handleEvent(Thing self, String name, ActionContext actionContext) {
		if(UtilData.isTrue(self.doAction("isPrintLog", actionContext))) {
			ChannelHandlerContext ctx = actionContext.getObject("ctx");
			Executor.info(TAG, self.getMetadata().getPath() + ":" + name + ": " + ctx);
		}
		
	}
	
	public static void handlerAdded(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		handleEvent(self, "handlerAdded", actionContext);
	}
	
	public static void handlerRemoved(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		handleEvent(self, "handlerRemoved", actionContext);
	}	
	
	public static void channelRegistered(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		handleEvent(self, "channelRegistered", actionContext);
	}
	
	public static void channelUnregistered(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		handleEvent(self, "channelUnregistered", actionContext);
	}
	
	public static void channelActive(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		handleEvent(self, "channelActive", actionContext);
	}

	public static void channelInactive(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		handleEvent(self, "channelInactive", actionContext);
	}
	
	public static void channelRead(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		handleEvent(self, "channelRead", actionContext);
	}

	public static void channelReadComplete(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		handleEvent(self, "channelReadComplete", actionContext);
	}

	public static void userEventTriggered(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		handleEvent(self, "userEventTriggered", actionContext);
	}
	
	public static void channelWritabilityChanged(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		handleEvent(self, "channelWritabilityChanged", actionContext);
	}

	public static void exceptionCaught(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		handleEvent(self, "exceptionCaught", actionContext);
	}
	
	public static void sslOperationComplete(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		handleEvent(self, "sslOperationComplete", actionContext);
	}
}
