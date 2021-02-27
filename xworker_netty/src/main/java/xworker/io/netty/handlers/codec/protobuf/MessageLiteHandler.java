package xworker.io.netty.handlers.codec.protobuf;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.google.protobuf.AbstractMessageLite;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MessageLiteHandler  extends SimpleChannelInboundHandler<AbstractMessageLite<?,?>> {
	Thing thing;
	ActionContext actionContext;
	
	public MessageLiteHandler(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		super.userEventTriggered(ctx, evt);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, AbstractMessageLite<?,?> message) throws Exception {
		thing.doAction("on" + message.getClass().getSimpleName(), actionContext);
	}

	public static MessageLiteHandler create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new MessageLiteHandler(self, actionContext);
	}

}
