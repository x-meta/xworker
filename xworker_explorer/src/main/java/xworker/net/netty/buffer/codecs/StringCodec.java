package xworker.net.netty.buffer.codecs;

import java.nio.charset.Charset;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import io.netty.buffer.ByteBuf;
import ognl.OgnlException;

public class StringCodec {
	public static void encode(ActionContext actionContext) throws OgnlException {
		Thing self = actionContext.getObject("self");
		ByteBuf buffer = actionContext.getObject("buffer");
		
		
		int lengthPrefix = self.doAction("getLengthPrefix", actionContext);
		String charset = self.doAction("getCharset", actionContext);
		//String value = self.doAction("getValue", actionContext);
		String value = (String) CodecUtils.getValue(self, actionContext);
		if(value == null) {
			value = "";
		}
		Charset cs = Charset.defaultCharset();
		if(charset != null) {
			cs = Charset.forName(charset);
		}
		
		byte[] bytes = null;
		if(value != null) {
			bytes = value.getBytes(cs);
		}
		
		CodecUtils.putLength(self, lengthPrefix, bytes.length, buffer);
		
		buffer.writeBytes(bytes);
	}
	
	public static void decode(ActionContext actionContext) throws OgnlException {
		Thing self = actionContext.getObject("self");
		ByteBuf buffer = actionContext.getObject("buffer");
		
		
		int lengthPrefix = self.doAction("getLengthPrefix", actionContext);
		String charset = self.doAction("getCharset", actionContext);
		
		Charset cs = Charset.defaultCharset();
		if(charset != null) {
			cs = Charset.forName(charset);
		}
		
		int length = CodecUtils.getLength(self, lengthPrefix, buffer);
		byte[] bytes = new byte[length];
		buffer.readBytes(bytes);
		
		String value = new String(bytes, cs);
		CodecUtils.setValue(self, value, actionContext); 
	}
}
