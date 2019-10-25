package xworker.net.netty.buffer.codecs;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import io.netty.buffer.ByteBuf;
import ognl.OgnlException;

public class BytesCodec {
	public static void encode(ActionContext actionContext) throws OgnlException {
		Thing self = actionContext.getObject("self");
		ByteBuf buffer = actionContext.getObject("buffer");
		
		
		int lengthPrefix = self.doAction("getLengthPrefix", actionContext);
		byte[] bytes = (byte[]) CodecUtils.getValue(self, actionContext);//t    self.doAction("getValue", actionContext);
		if(bytes == null) {
			bytes = new byte[0];
		}
		if(lengthPrefix > 0) {
			CodecUtils.putLength(self, lengthPrefix, bytes.length, buffer);
		}
		
		buffer.writeBytes(bytes);
	}
	
	public static void decode(ActionContext actionContext) throws OgnlException {
		Thing self = actionContext.getObject("self");
		ByteBuf buffer = actionContext.getObject("buffer");
		
		
		int lengthPrefix = self.doAction("getLengthPrefix", actionContext);		
		int length = 0;//CodecUtils.getLength(self, lengthPrefix, buffer);
		if(lengthPrefix > 0) {
			length = CodecUtils.getLength(self, lengthPrefix, buffer);
		}else {
			length = buffer.readableBytes();
		}
		
		byte[] bytes = new byte[length];
		buffer.readBytes(bytes);
		
		CodecUtils.setValue(self, bytes, actionContext); 
	}
}
