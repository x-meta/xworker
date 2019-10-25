package xworker.io;

import java.nio.ByteBuffer;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.util.UtilData;

public class ByteBufferActions {
	public static Object byteBufferBuilder(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		ByteBuffer buffer = self.doAction("getBuffer", actionContext);
		if(buffer == null) {
			int capacity = self.doAction("getCapacity", actionContext);
			boolean direct = self.doAction("isDirect", actionContext);
			
			if(direct) {
				buffer = ByteBuffer.allocateDirect(capacity);
			}else {
				buffer = ByteBuffer.allocate(capacity);
			}
		}
		
		String varName = self.doAction("getVarName", actionContext);
		if(varName != null && !"".equals(varName)) {
			actionContext.peek().put(varName, buffer);
		}
		
		for(Thing child : self.getChilds()) {
			if("actions".equals(child.getMetadata().getName())) {
				continue;
			}
			
			Object obj = child.getAction().run(actionContext);
			if(obj == null) {
				continue;
			}
			
			if(obj instanceof Byte) {
				buffer.put((Byte) obj);
			}else if(obj instanceof byte[]) {
				buffer.put((byte[]) obj);
			}else if(obj instanceof ByteBuffer) {
				buffer.put((ByteBuffer ) obj);
			}else if(obj instanceof Character) {
				buffer.putChar((Character) obj);
			}else if(obj instanceof Double) {
				buffer.putDouble((Double) obj);
			}else if(obj instanceof Float) {
				buffer.putFloat((Float) obj);
			}else if(obj instanceof Integer) {
				buffer.putInt((Integer) obj);
			}else if(obj instanceof Long) {
				buffer.putLong((Long) obj);
			}else if(obj instanceof Short) {
				buffer.putShort((Short) obj);
			}
		}
		
		//是否反转
		if(UtilData.isTrue(self.doAction("isFlip", actionContext))) {
			buffer.flip();
		}
		
		return buffer;
	}
	
	public static int capacity(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		ByteBuffer buffer = self.doAction("getBuffer", actionContext);
		return buffer.capacity();				
	}
	
	public static Object clear(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		ByteBuffer buffer = self.doAction("getBuffer", actionContext);
		return buffer.clear();				
	}
	
	public static Object flip(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		ByteBuffer buffer = self.doAction("getBuffer", actionContext);
		return buffer.flip();				
	}
	
	public static Object hasRemaining(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		ByteBuffer buffer = self.doAction("getBuffer", actionContext);
		return buffer.hasRemaining();				
	}
	
	public static Object isDirect(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		ByteBuffer buffer = self.doAction("getBuffer", actionContext);
		return buffer.isDirect();				
	}
	
	public static Object isReadOnly(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		ByteBuffer buffer = self.doAction("getBuffer", actionContext);
		return buffer.isReadOnly();				
	}
	
	public static Object limit(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		ByteBuffer buffer = self.doAction("getBuffer", actionContext);
		return buffer.limit();				
	}
	
	public static Object mark(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		ByteBuffer buffer = self.doAction("getBuffer", actionContext);
		return buffer.mark();				
	}
	
	public static Object position(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		ByteBuffer buffer = self.doAction("getBuffer", actionContext);
		return buffer.position();				
	}
	
	public static Object remaining(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		ByteBuffer buffer = self.doAction("getBuffer", actionContext);
		return buffer.remaining();				
	}
	
	public static Object reset(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		ByteBuffer buffer = self.doAction("getBuffer", actionContext);
		return buffer.reset();				
	}
	
	public static Object rewind(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		ByteBuffer buffer = self.doAction("getBuffer", actionContext);
		return buffer.rewind();				
	}
}
