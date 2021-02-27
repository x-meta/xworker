package xworker.io.netty.handlers.codec.json;

import java.util.List;

import org.xmeta.ActionContext;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import xworker.util.JacksonFormator;

@Sharable
public class JsonEncoder extends MessageToMessageEncoder<Object> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
		if(msg instanceof ByteBuf) {
			out.add(msg);
		}else {		
			String json = JacksonFormator.formatObject(msg);
			out.add(Unpooled.wrappedBuffer(json.getBytes()));
		}
	}
	
	public static JsonEncoder create(ActionContext actionContext) {
		return new JsonEncoder();
	}
}
