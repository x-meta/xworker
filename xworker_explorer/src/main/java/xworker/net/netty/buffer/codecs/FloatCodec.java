package xworker.net.netty.buffer.codecs;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import io.netty.buffer.ByteBuf;
import ognl.OgnlException;
import xworker.util.UtilData;

public class FloatCodec {
	public static void encode(ActionContext actionContext) throws OgnlException {
		Thing self = actionContext.getObject("self");
		ByteBuf buffer = actionContext.getObject("buffer");
		
		float value = UtilData.getFloat(CodecUtils.getValue(self, actionContext), 0f);
		//float value = self.doAction("getValue", actionContext);
		buffer.writeFloat(value);		
	}
	
	public static void decode(ActionContext actionContext) throws OgnlException {
		Thing self = actionContext.getObject("self");
		ByteBuf buffer = actionContext.getObject("buffer");
		
		float value = buffer.readFloat();
		CodecUtils.setValue(self, value, actionContext); 
	}
}
