package xworker.net.netty.buffer.codecs;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import io.netty.buffer.ByteBuf;
import ognl.OgnlException;
import xworker.util.UtilData;

public class LongCodec {
	public static void encode(ActionContext actionContext) throws OgnlException {
		Thing self = actionContext.getObject("self");
		ByteBuf buffer = actionContext.getObject("buffer");
		
		long value = UtilData.getLong(CodecUtils.getValue(self, actionContext), 0);
		//long value = self.doAction("getValue", actionContext);
		buffer.writeLong(value);		
	}
	
	public static void decode(ActionContext actionContext) throws OgnlException {
		Thing self = actionContext.getObject("self");
		ByteBuf buffer = actionContext.getObject("buffer");
		
		long value = buffer.readLong();
		CodecUtils.setValue(self, value, actionContext); 
	}
}
