package xworker.io.netty.handlers.codec.json;

import java.util.List;

import org.xmeta.ActionContext;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import xworker.util.JacksonFormator;

@Sharable
public class JsonDecoder extends MessageToMessageDecoder<Object> {

	@Override
	protected void decode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
		if(msg instanceof ByteBuf) {
			ByteBuf buf = (ByteBuf) msg;
			final byte[] array;
	        final int offset;
	        final int length = buf.readableBytes();
	        if (buf.hasArray()) {
	            array = buf.array();
	            offset = buf.arrayOffset() + buf.readerIndex();
	        } else {
	            array = new byte[length];
	            buf.getBytes(buf.readerIndex(), array, 0, length);
	            offset = 0;
	        }
	        
	        out.add(JacksonFormator.parseObject(new String(array, offset, length)));
		}else if(msg instanceof String) {
			out.add(JacksonFormator.parseObject((String) msg));
		}
	}
	
	public static JsonDecoder create(ActionContext actionContext) {
		return new JsonDecoder();
	}

}
