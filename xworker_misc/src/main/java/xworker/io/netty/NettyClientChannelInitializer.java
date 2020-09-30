package xworker.io.netty;

import org.xmeta.Thing;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class NettyClientChannelInitializer  extends ChannelInitializer<SocketChannel> {
	NettyClient client;

	public NettyClientChannelInitializer(NettyClient client) {
		this.client = client;
	}

	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		
		for(Thing handlers : client.getThing().getChilds("Handlers")) {								
			for(Thing handlerThing : handlers.getChilds()) {
				Object handler = handlerThing.doAction("create", client.getActionContext(), "channel", ch);
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
}
