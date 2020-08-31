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
import xworker.lang.executor.Executor;

public class NettyClient {
	private static final String TAG = NettyClient.class.getName();

	Thing thing;
	ActionContext actionContext;
	private ChannelFuture channelFuture;
	String host;
	int port;

	public NettyClient(Thing thing, ActionContext parentContext) {
		this.thing = thing;
		actionContext = new ActionContext();
		actionContext.put("parentContext", parentContext);
	}

	public void start() {
		if (isStarted()) {
			return;
		}

		host = thing.doAction("getHost", actionContext);
		port = thing.doAction("getPort", actionContext);
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline pipeline = ch.pipeline();

							for (Thing handlers : thing.getChilds("Handlers")) {
								for (Thing handlerThing : handlers.getChilds()) {
									Object handler = handlerThing.doAction("create", actionContext, "channel", ch);
									if (handler instanceof ChannelHandler) {

										pipeline.addLast(handlerThing.getMetadata().getName(),
												(ChannelHandler) handler);
									}
								}
							}
						}
					});

			channelFuture = bootstrap.bind(port);
			channelFuture.addListener(new GenericFutureListener<ChannelFuture>() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if (future.isSuccess()) {
						thing.doAction("startSuccess", actionContext, "nettyClient", this);
					} else if (future.cause() != null) {
						thing.doAction("startFailure", actionContext, "nettyClient", this, "cause", future.cause());
					} else if (future.isCancelled()) {
						thing.doAction("startCancelled", actionContext, "nettyClient", this);
					}
				}

			});

			channelFuture.channel().closeFuture().addListener(new GenericFutureListener<ChannelFuture>() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					thing.doAction("closed", actionContext, "nettyClient", this);
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

	public boolean isStarted() {
		return channelFuture != null && (channelFuture.isDone() == false || channelFuture.isSuccess());
	}

	public boolean isClosed() {
		if (channelFuture == null) {
			return true;
		}

		if (channelFuture.isDone() && (channelFuture.cause() != null || channelFuture.isCancelled())) {
			return true;
		}

		if (channelFuture.channel().closeFuture().isDone()) {
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
		if (!isClosed()) {
			channelFuture.channel().close();
		}
	}

	public static void startSuccess(ActionContext actionContext) {
		NettyClient client = actionContext.getObject("nettyClient");
		Executor.info(TAG, "Netty client({}) started at {}.", client.getThing().getMetadata().getPath(),
				client.getPort());
	}

	public static void startFailure(ActionContext actionContext) {
		NettyClient client = actionContext.getObject("nettyClient");
		Throwable cause = actionContext.getObject("cause");
		Executor.warn(TAG, "Netty client(" + client.getThing().getMetadata().getPath() + ") started failure", cause);

	}

	public static void startCancelled(ActionContext actionContext) {
		NettyClient client = actionContext.getObject("nettyClient");
		Executor.info(TAG, "Netty client({}) start cancelled.", client.getThing().getMetadata().getPath());
	}

	public static void closed(ActionContext actionContext) {
		NettyClient client = actionContext.getObject("nettyClient");
		Executor.info(TAG, "Netty client({}) is closed.", client.getThing().getMetadata().getPath());
	}

	public static NettyClient create(ActionContext actionContext) {
		String key = "nettyClient";
		Thing self = actionContext.getObject("self");
		NettyClient nettyClient = self.getData(key);

		if (nettyClient == null) {
			nettyClient = new NettyClient(self, actionContext);
			self.setData(key, nettyClient);
		}

		actionContext.g().put(self.getMetadata().getName(), nettyClient);

		return nettyClient;
	}

	public static NettyClient start(ActionContext actionContext) {
		NettyClient client = create(actionContext);
		client.start();
		return client;
	}

	public static NettyClient close(ActionContext actionContext) {
		NettyClient client = create(actionContext);
		client.close();
		return client;
	}

}
