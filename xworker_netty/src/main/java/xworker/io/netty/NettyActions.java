package xworker.io.netty;

import java.io.IOException;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import io.netty.channel.ChannelHandlerContext;

public class NettyActions {
	public static NettySession getNettySession(ActionContext actionContext) {
		ChannelHandlerContext ctx = actionContext.getObject("ctx");
		return NettySession.getSession(ctx.channel());
	}
	
	public static NettySessionManager getNettySessionManager(ActionContext actionContext) {
		ChannelHandlerContext ctx = actionContext.getObject("ctx");
		return NettySession.getSession(ctx.channel()).getSessionManager();
	}
	
	public static void sendMessageToSession(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		
		ChannelHandlerContext ctx = actionContext.getObject("ctx");
		NettySessionManager sessionManager = NettySession.getSession(ctx.channel()).getSessionManager();
		String sessionId = self.doAction("getSessionId", actionContext);
		Object message = self.doAction("getMessage", actionContext);
		sessionManager.sendMessage(sessionId, message);
	}
	
	public static void sendMessageToGroup(ActionContext actionContext) throws IOException {
		Thing self = actionContext.getObject("self");
		
		ChannelHandlerContext ctx = actionContext.getObject("ctx");
		NettySessionManager sessionManager = NettySession.getSession(ctx.channel()).getSessionManager();
		String groupPath = self.doAction("getGroupPath", actionContext);
		Object message = self.doAction("getMessage", actionContext);
		sessionManager.sendMessageToGroup(groupPath, message);
	}
}
