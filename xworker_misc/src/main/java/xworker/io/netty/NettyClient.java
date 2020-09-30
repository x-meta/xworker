package xworker.io.netty;

import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
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
		Map<String, Object> variables = thing.doAction("getVariables", parentContext);
		if(variables != null) {
			actionContext.putAll(variables);
		}
		
		actionContext.put("parentContext", parentContext);
		
		host = thing.doAction("getHost", actionContext);
		port = thing.doAction("getPort", actionContext);				
	}

	public void connect() {
		if (isConnected()) {
			return;
		}
		
		int connectTimeOut = thing.doAction("getConnectTimeOut", actionContext);
		
		NioEventLoopGroup workerGroup = new NioEventLoopGroup(1);
				
		try {
			Bootstrap b = new Bootstrap(); 
	        b.group(workerGroup);
	        for(Thing channels : thing.getAllChilds("Channels")) {
	        	for(Thing channel : channels.getChilds()) {
	        		channel.doAction("create", actionContext, "bootstrap", b);
	        	}
	        }
	        b.channel(NioSocketChannel.class); 
	        b.option(ChannelOption.SO_KEEPALIVE, true); 
	        b.option(ChannelOption.SO_RCVBUF, 1024 * 1204 * 2);
	        b.option(ChannelOption.TCP_NODELAY, true);

	        if(connectTimeOut > 0) {
	        	b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeOut);
	        }
	        b.handler(new NettyClientChannelInitializer(this));
	         
	        // Start the client.	        
			channelFuture = b.connect(host, port).sync();			
			channelFuture.addListener(new GenericFutureListener<ChannelFuture>() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if(future.isSuccess()) {
						thing.doAction("startSuccess", actionContext, "nettyClient", NettyClient.this);
					}else if(future.cause() != null) {
						thing.doAction("startFailure", actionContext, "nettyClient", NettyClient.this, "cause", future.cause());
					}else if(future.isCancelled()) {
						thing.doAction("startCancelled", actionContext, "nettyClient", NettyClient.this);
					}
				}
				
			});
			
			channelFuture.channel().closeFuture().addListener(new GenericFutureListener<ChannelFuture>() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					workerGroup.shutdownGracefully();
					
					thing.doAction("closed", actionContext, "nettyClient", NettyClient.this);
				}
				
			});
			
			//thing.doAction("startSuccess", actionContext, "nettyClient", NettyClient.this);
        } catch (Exception e) {
			workerGroup.shutdownGracefully();
			
			thing.doAction("startFailure", actionContext, "nettyClient", NettyClient.this, "cause", e);
		}
	}

	public boolean isConnected() {
		if(isClosed()) {
			return false;
		}
		
		return channelFuture != null && (channelFuture.isDone() == true && channelFuture.isSuccess());
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
	
	public void sendMessage(Object message, boolean flush){
		if(!isClosed()) {
			channelFuture.channel().write(message);
			if(flush) {
				channelFuture.channel().flush();
			}
		}
	}
	
	public void sendMessage(Object message) {
		sendMessage(message, true);
	}
	
	public void flush() {
		if(!isClosed()) {
			channelFuture.channel().flush();
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
		NettyClient nettyClient = null;
		if(UtilData.isTrue(self.doAction("isSingleInstance", actionContext))) {
			nettyClient = self.getData(key);
	
			if (nettyClient == null) {
				nettyClient = new NettyClient(self, actionContext);
				self.setData(key, nettyClient);
			}
		}else {
			nettyClient = new NettyClient(self, actionContext);
		}

		return nettyClient;
	}

	public static void remove(ActionContext actionContext) {
		String key = "nettyClient";
		Thing self = actionContext.getObject("self");
		NettyClient nettyClient = self.getData(key);
		if(nettyClient != null) {
			self.setData(key, null);
			nettyClient.close();
		}
	}
	
	public static NettyClient connect(ActionContext actionContext) {
		NettyClient client = create(actionContext);
		client.connect();
		return client;
	}

	public static NettyClient close(ActionContext actionContext) {
		NettyClient client = create(actionContext);
		client.close();
		return client;
	}
	
	public static NettyClient getNettyClient(ActionContext actionContext) {
		return create(actionContext);
	}
	
	public static NettyClient run(ActionContext actionContext) {
		NettyClient client = create(actionContext);
		if(client != null && client.isConnected() == false) {
			client.connect();
		}
		
		return client;
	}

}
