package xworker.io.netty.handlers.mqtt;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;

public class MqttHandler extends SimpleChannelInboundHandler<MqttMessage>{
	Thing thing;
	ActionContext actionContext;
	
	public MqttHandler(Thing thing, ActionContext actionContext) {
		this.thing = thing;
		this.actionContext = actionContext;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, MqttMessage msg) throws Exception {
		thing.doAction("onMessage", actionContext, "msg", msg);
				
		MqttMessageType type = msg.fixedHeader().messageType();
		String name = type.name().toLowerCase();
		thing.doAction(name, actionContext, "msg", msg);		
	}
	
	public static MqttHandler create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		return new MqttHandler(self, actionContext);
	}

}
