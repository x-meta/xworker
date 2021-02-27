package xworker.io.netty.handlers.codec.protobuf;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.google.protobuf.AbstractMessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MessageHandler extends SimpleChannelInboundHandler<AbstractMessage> {
	Thing thing;
	ActionContext actionContext;
	
	public MessageHandler(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		thing.doAction("userEventTriggered", actionContext, "ctx", ctx, "evt", evt);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		thing.doAction("exceptionCaught", actionContext, "ctx", ctx, "cause", cause);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, AbstractMessage message) throws Exception {
		thing.doAction("onMessage", actionContext, "ctx", ctx, "message", message);
	}

	public static MessageHandler create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new MessageHandler(self, actionContext);
	}
	
	public static void onMessage(ActionContext actionContext) {
		
	}
}
