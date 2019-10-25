package xworker.net.netty.buffer.codecs;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;

import io.netty.buffer.ByteBuf;
import ognl.Ognl;
import ognl.OgnlException;

public class CodecUtils {
	
	public static Object getValue(Thing thing, ActionContext actionContext) throws OgnlException {
		Object object = actionContext.get("object");
		String name = thing.getMetadata().getName();
		return Ognl.getValue(name, object);
	}
	
	public static void setValue(Thing thing, Object value, ActionContext actionContext) throws OgnlException {
		Object object = actionContext.get("object");
		String name = thing.getMetadata().getName();
		Ognl.setValue(name, object, value);
	}
	
	public static void putLength(Thing self, int lengthPrefix, int length, ByteBuf buffer) {	
		if(lengthPrefix == 1) {
			if(length > 255) {
				throw new ActionException("Length more than 255, path=" + self.getMetadata().getPath());
			}
			
			buffer.writeByte((byte) (length & 0xFF));
		}else if(lengthPrefix == 2) {
			if(length > 65546) {
				throw new ActionException("Length more than 65536, path=" + self.getMetadata().getPath());
			}
			
			buffer.writeShort((short) (length & 0xFF));
		}else if(lengthPrefix == 4) {
			buffer.writeInt(length);
		}
	}
	
	public static int getLength(Thing self, int lengthPrefix, ByteBuf buffer) {
		if(lengthPrefix == 1) {
			return buffer.readByte() & 0xFF;
		}else if(lengthPrefix == 2) {
			return buffer.readShort() & 0xFFFF;
		}else if(lengthPrefix == 4) {
			return buffer.readInt();
		}else {
			return 0;
		}
	}
}
