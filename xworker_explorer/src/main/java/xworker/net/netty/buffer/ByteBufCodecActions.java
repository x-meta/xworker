package xworker.net.netty.buffer;

import java.util.HashMap;

import org.xmeta.ActionContext;
import org.xmeta.ActionException;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ByteBufCodecActions {
	public static ByteBuf createBuffer(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		int initialCapacity = self.doAction("getInitialCapacity", actionContext);
		int maxCapacity = self.doAction("getMaxCapacity", actionContext);
		
		if(initialCapacity > 0 && maxCapacity > 0) {
			return Unpooled.buffer(initialCapacity, maxCapacity);
		}else if(initialCapacity > 0 ) {
			return Unpooled.buffer(initialCapacity);
		}else {
			return Unpooled.buffer();
		}
	}
	
	public static Object encode(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		//初始化变量
		Object obj = self.doAction("getObject", actionContext);
		ByteBuf buffer = self.doAction("getBuffer", actionContext);
		boolean selfCreated  = false;
		if(buffer == null) {
			buffer = self.doAction("createBuffer", actionContext);
			selfCreated = true;
		}
		
		if(buffer == null) {
			throw new ActionException("Buffer is null, codec=" + self.getMetadata().getPath());
		}
		
		String returnType = self.doAction("getReturnType", actionContext);
		try {
			//执行子节点编码
			actionContext.peek().put("object", obj);
			actionContext.peek().put("buffer", buffer);
			
			Thing codec = self.doAction("getByteBufCodec", actionContext);
			codec.doAction("encode", actionContext);
			
			//返回类型		
			if("byte[]".equals(returnType)) {			
				byte[] bytes = new byte[buffer.readableBytes()];
				buffer.readBytes(bytes);
				
				return bytes;
			}else {
				return buffer;
			}
		} finally {
			if(UtilData.isTrue(self.doAction("isReleaseBuffer", actionContext))) {
				buffer.release();
			}else if(selfCreated && "byte[]".equals(returnType)) {
				buffer.release();
			}
		}
	}
	
	public static Object decode(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		//初始化变量
		Object obj = self.doAction("createObject", actionContext);
		if(obj == null) {
			obj = new HashMap<String, Object>();
		}
		Object objBuffer = self.doAction("getBuffer", actionContext); 
		ByteBuf buffer = null;
		if(objBuffer instanceof byte[]) {
			buffer = Unpooled.wrappedBuffer((byte[]) objBuffer);
		}else if(objBuffer instanceof ByteBuf) {
			buffer = (ByteBuf) objBuffer;
		}
		
		if(buffer == null) {
			throw new ActionException("Buffer is null, codec=" + self.getMetadata().getPath());
		}
		
		try {
			//执行子节点编码
			actionContext.peek().put("object", obj);
			actionContext.peek().put("buffer", buffer);
			
			Thing codec = self.doAction("getByteBufCodec", actionContext);
			codec.doAction("decode", actionContext);
			return obj;
		}finally {
			if(UtilData.isTrue(self.doAction("isReleaseBuffer", actionContext))) {
				buffer.release();
			}
		}
	}
}
