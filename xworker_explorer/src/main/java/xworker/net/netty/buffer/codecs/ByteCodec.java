package xworker.net.netty.buffer.codecs;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import io.netty.buffer.ByteBuf;
import ognl.OgnlException;
import xworker.util.UtilData;

public class ByteCodec {
	public static void encode(ActionContext actionContext) throws OgnlException {
		Thing self = actionContext.getObject("self");
		ByteBuf buffer = actionContext.getObject("buffer");
		
		byte value = UtilData.getByte(CodecUtils.getValue(self, actionContext), (byte) 0);		
		buffer.writeByte(value);		
	}
	
	public static void decode(ActionContext actionContext) throws OgnlException {
		Thing self = actionContext.getObject("self");
		ByteBuf buffer = actionContext.getObject("buffer");
		
		byte value = buffer.readByte();
		CodecUtils.setValue(self, value, actionContext); 
	}
}
