package xworker.net.netty.buffer.codecs;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import io.netty.buffer.ByteBuf;
import ognl.OgnlException;
import xworker.util.UtilData;

public class IntCodec {
	public static void encode(ActionContext actionContext) throws OgnlException {
		Thing self = actionContext.getObject("self");
		ByteBuf buffer = actionContext.getObject("buffer");
		
		int value = UtilData.getInt(CodecUtils.getValue(self, actionContext), 0);
		//int value = self.doAction("getValue", actionContext);
		buffer.writeInt(value);		
	}
	
	public static void decode(ActionContext actionContext) throws OgnlException {
		Thing self = actionContext.getObject("self");
		ByteBuf buffer = actionContext.getObject("buffer");
		
		int value = buffer.readInt();
		CodecUtils.setValue(self, value, actionContext); 
	}
}
