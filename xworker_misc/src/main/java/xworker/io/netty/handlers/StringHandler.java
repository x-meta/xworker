package xworker.io.netty.handlers;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class StringHandler  extends SimpleChannelInboundHandler<String>{
	Thing thing;
	ActionContext actionContext;
	
	public StringHandler(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		thing.doAction("onMessage", actionContext, "msg", msg);	
	}
	
	public static StringHandler create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new StringHandler(self, actionContext);
	}
}
