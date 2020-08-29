package xworker.io.netty;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;

public class NettyServer {
	Thing thing;
	ActionContext actionContext;
	private ChannelFuture channelFuture;
	
	public NettyServer(Thing thing, ActionContext parentContext) {
		this.thing = thing;
		actionContext = new ActionContext();
		actionContext.put("parentContext", parentContext);
		
		init();
	}
	
	private void init() {
		EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap(); // (2)
			bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class) // (3)
					.childHandler(new ChannelInitializer<SocketChannel>() { // (4)
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline pipeline = ch.pipeline();
							
							for(Thing handlers : thing.getChilds("Handlers")) {								
								for(Thing handlerThing : handlers.getChilds()) {
									Object handler = handlerThing.doAction("create", actionContext, "channel", ch);
									if(handler instanceof ChannelHandler) {
										
										pipeline.addLast(handlerThing.getMetadata().getName(), (ChannelHandler) handler);
									}
								}
							}
						}
					});
			
			int port = thing.doAction("getPort", actionContext);
			
			// Bind and start to accept incoming connections.
			channelFuture = bootstrap.bind(port);
			channelFuture.addListener(new GenericFutureListener<ChannelFuture>() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if(future.isSuccess()) {
						thing.doAction("startSuccess", actionContext, "nettyServer", this);
					}else if(future.cause() != null) {
						thing.doAction("startFailure", actionContext, "nettyServer", this, "cause", future.cause());
					}else if(future.isCancelled()) {
						thing.doAction("startCancelled", actionContext, "nettyServer", this);
					}
				}
				
			});
			
			// Wait until the server socket is closed.
			// In this example, this does not happen, but you can do that to gracefully
			// shut down your server.
			channelFuture.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
	
	public boolean isStarted() {
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
}
