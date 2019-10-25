package xworker.net.netty.buffer.codecs;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import io.netty.buffer.ByteBuf;
import ognl.OgnlException;

public class DoubleCodec {
	public static void encode(ActionContext actionContext) throws OgnlException {
		Thing self = actionContext.getObject("self");
		ByteBuf buffer = actionContext.getObject("buffer");
		
		double value = UtilData.getDouble(CodecUtils.getValue(self, actionContext), 0);
		//double value = self.doAction("getValue", actionContext);
		buffer.writeDouble(value);		
	}
	
	public static void decode(ActionContext actionContext) throws OgnlException {
		Thing self = actionContext.getObject("self");
		ByteBuf buffer = actionContext.getObject("buffer");
		
		double value = buffer.readDouble();
		CodecUtils.setValue(self, value, actionContext); 
	}
}
