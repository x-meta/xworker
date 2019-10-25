package xworker.net.netty.buffer.codecs;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import io.netty.buffer.ByteBuf;
import ognl.OgnlException;

public class ShortCodec {
	public static void encode(ActionContext actionContext) throws OgnlException {
		Thing self = actionContext.getObject("self");
		ByteBuf buffer = actionContext.getObject("buffer");
		
		short value = UtilData.getShort(CodecUtils.getValue(self, actionContext), (short) 0);
		//short value = self.doAction("getValue", actionContext);
		buffer.writeShort(value);		
	}
	
	public static void decode(ActionContext actionContext) throws OgnlException {
		Thing self = actionContext.getObject("self");
		ByteBuf buffer = actionContext.getObject("buffer");
		
		short value = buffer.readShort();
		CodecUtils.setValue(self, value, actionContext); 
	}
}
