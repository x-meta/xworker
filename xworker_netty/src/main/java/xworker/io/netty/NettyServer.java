package xworker.io.netty;

import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import xworker.lang.executor.Executor;

public class NettyServer {
	private static final String TAG = NettyServer.class.getName();
	
	Thing thing;
	ActionContext actionContext;
	private ChannelFuture channelFuture;
	int port;
	NettySessionManager sessionManager = new NettySessionManager();
	
	public NettyServer(Thing thing, ActionContext parentContext) {
		this.thing = thing;
		actionContext = new ActionContext();
		Map<String, Object> variables = thing.doAction("getVariables", parentContext);
		if(variables != null) {
			actionContext.putAll(variables);
		}
		
		actionContext.put("parentContext", parentContext);
		actionContext.put("sessionManager", sessionManager);
	}
	
	public void start() {
		if(isStarted()) {
			return;
		}
		
	    port = thing.doAction("getPort", actionContext);
		EventLoopGroup bossGroup = new NioEventLoopGroup(); 
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap(); 
			for(Thing channels : thing.getAllChilds("Channels")) {
	        	for(Thing channel : channels.getChilds()) {
	        		channel.doAction("create", actionContext, "bootstrap", bootstrap);
	        	}
	        }

			bootstrap.group(bossGroup, workerGroup);
			if(port > 0) {
				bootstrap.channel(NioServerSocketChannel.class);
			}
			
			for(Thing handlers : thing.getChilds("Handler")) {								
				for(Thing handlerThing : handlers.getChilds()) {
					Object handler = handlerThing.doAction("create", actionContext);
					if(handler instanceof ChannelHandler) {										
						bootstrap.handler((ChannelHandler) handler);
					}else if(handler instanceof ChannelHandler[]) {
						ChannelHandler hds[] = (ChannelHandler[]) handler;
						for(int i=0; i<hds.length ; i++) {
							if(hds[i] != null) {
								bootstrap.handler((ChannelHandler) hds[i]);
							}
						}
					}
				}
			}
			
			bootstrap.childHandler(new ChannelInitializer<Channel>() { 
						@Override
						public void initChannel(Channel ch) throws Exception {
							ChannelPipeline pipeline = ch.pipeline();
							NettySession session = new NettySession(sessionManager, ch);
							session.setAttribute(NettySession.KEY, session);
							
							pipeline.addLast(new ChannelInboundHandlerAdapter() {
								@Override
								public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

									thing.doAction("sessionConnected", actionContext, "session", session, "ctx", ctx);
								}

								@Override
								public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
									super.channelUnregistered(ctx);
									
									//Channel channel = ctx.channel();
									//System.out.println("客户端断开：" + channel);
									//NettySession session = NettySession.getSession(channel);
									
									thing.doAction("sessionClosed", actionContext, "session", session, "ctx", ctx);
									
									session.dispose();																	
								}

								@Override
								public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
										throws Exception {
									thing.doAction("exceptionCaught", actionContext, "session", session, "ctx", ctx, "cause", cause);
								}
							});
							
							for(Thing handlers : thing.getChilds("Handlers")) {
								Object handler = null;
								for(Thing handlerThing : handlers.getChilds()) {
									//try {
										handler = handlerThing.doAction("create", actionContext, "channel", ch);
									//}catch(Throwable e) {										
									//	throw new ActionException("Init handler error, handler=" + handlerThing.getMetadata().getPath() + "\n"
									//			+ ExceptionUtil.getRootMessage(e));
									//}
									if(handler instanceof ChannelHandler) {										
										pipeline.addLast(handlerThing.getMetadata().getName(), (ChannelHandler) handler);
									}else if(handler instanceof ChannelHandler[]) {
										ChannelHandler hds[] = (ChannelHandler[]) handler;
										for(int i=0; i<hds.length ; i++) {
											if(hds[i] != null) {
												pipeline.addLast(handlerThing.getMetadata().getName() + "_" + i, hds[i]);
											}
										}
									}
								}
							}
							
						}
					});
			
			channelFuture = bootstrap.bind(port);
			channelFuture.addListener(new GenericFutureListener<ChannelFuture>() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if(future.isSuccess()) {
						thing.doAction("startSuccess", actionContext, "nettyServer", NettyServer.this);
					}else if(future.cause() != null) {
						thing.doAction("startFailure", actionContext, "nettyServer", NettyServer.this, "cause", future.cause());
					}else if(future.isCancelled()) {
						thing.doAction("startCancelled", actionContext, "nettyServer", NettyServer.this);
					}
				}
				
			});
			
			channelFuture.channel().closeFuture().addListener(new GenericFutureListener<ChannelFuture>() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					workerGroup.shutdownGracefully();
					bossGroup.shutdownGracefully();
					thing.doAction("closed", actionContext, "nettyServer", NettyServer.this);
				}
				
			});
			
			//thing.doAction("startSuccess", actionContext, "nettyServer", NettyServer.this);
		} catch (Exception e) {
			thing.doAction("startFailure", actionContext, "nettyServer", NettyServer.this, "cause", e);
		}
	}
	
	public boolean isStarted() {
		if(isClosed()) {
			return false;
		}
		
		return channelFuture != null && (channelFuture.isDone() == false  || channelFuture.isSuccess());
	}
	
	public boolean isClosed() {
		if(channelFuture == null) {
			return true;
		}
		
		if(channelFuture.isDone() && (channelFuture.cause() != null || channelFuture.isCancelled())){
			return true;
		}
		
		if(channelFuture.channel().closeFuture().isDone()) {
			return true;
		}
		
		return false;
	}

	public Thing getThing() {
		return thing;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}

	public ChannelFuture getChannelFuture() {
		return channelFuture;
	}
	
	public int getPort() {
		return port;
	}
	
	public void close() {
		if(!isClosed()) {
			channelFuture.channel().close();
		}
	}
	
	public static void startSuccess(ActionContext actionContext) {
		NettyServer server = actionContext.getObject("nettyServer");
		Executor.info(TAG, "Netty server({}) started at {}.", server.getThing().getMetadata().getPath(),  server.getPort());
	}
	
	public static void startFailure(ActionContext actionContext) {
		NettyServer server = actionContext.getObject("nettyServer");
		Throwable cause = actionContext.getObject("cause");
		Executor.warn(TAG, "Netty serer(" + server.getThing().getMetadata().getPath() + ") started failure", cause);

	}
	
	public static void startCancelled(ActionContext actionContext) {
		NettyServer server = actionContext.getObject("nettyServer");
		Executor.info(TAG, "Netty server({}) start cancelled.", server.getThing().getMetadata().getPath());
	}
	
	public static void closed(ActionContext actionContext) {
		NettyServer server = actionContext.getObject("nettyServer");
		Executor.info(TAG, "Netty server({}) is closed.", server.getThing().getMetadata().getPath());
	}
	
	public static void exceptionCaught(ActionContext actionContext) {
		NettyServer server = actionContext.getObject("nettyServer");
		Throwable cause = actionContext.getObject("cause");
		Executor.info(TAG, "Netty server({}) exceptionCaught.", server.getThing().getMetadata().getPath(), cause);
	}
		
	public static NettyServer create(ActionContext actionContext) {
		String key = "nettyServer";
		Thing self = actionContext.getObject("self");
		NettyServer nettyServer = self.getData(key);
		
		if(nettyServer == null) {
			nettyServer = new NettyServer(self, actionContext);
			self.setData(key, nettyServer);
		}
		
		//actionContext.g().put(self.getMetadata().getName(), nettyServer);
		
		return nettyServer;
	}
	
	
	public static NettyServer getNettyServer(ActionContext actionContext) {
		return create(actionContext);
	}
	
	public static  NettyServer start(ActionContext actionContext) {
		NettyServer server = create(actionContext);
		server.start();
		return server;
	}
	
	public static NettyServer close(ActionContext actionContext) {
		NettyServer server = create(actionContext);
		server.close();
		return server;
	}
}
