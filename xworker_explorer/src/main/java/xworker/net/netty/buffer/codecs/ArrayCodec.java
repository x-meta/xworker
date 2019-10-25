package xworker.net.netty.buffer.codecs;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import io.netty.buffer.ByteBuf;
import ognl.OgnlException;
import xworker.util.UtilData;

public class ArrayCodec {
	public static void encode(ActionContext actionContext) throws OgnlException {
		Thing self = actionContext.getObject("self");
		ByteBuf buffer = actionContext.getObject("buffer");
				
		int lengthPrefix = self.doAction("getLengthPrefix", actionContext);
		Object array =  CodecUtils.getValue(self, actionContext);
		
		int length = Array.getLength(array);
		CodecUtils.putLength(self, lengthPrefix, length, buffer);
		String type = self.getStringBlankAsNull("type");
		
		for(int i=0;i <length; i++) {
			Object obj = Array.get(array, i);
			if(type != null) {
				obj = UtilData.toMap("value", obj);
			}
			actionContext.peek().put("object", obj);
			
			for(Thing child : self.getChilds("Codec")) {
				child.doAction("encode", actionContext);
			}
		}		
	}
	
	@SuppressWarnings("unchecked")
	public static void decode(ActionContext actionContext) throws OgnlException {
		Thing self = actionContext.getObject("self");
		ByteBuf buffer = actionContext.getObject("buffer");
				
		int lengthPrefix = self.doAction("getLengthPrefix", actionContext);
		int length = CodecUtils.getLength(self, lengthPrefix, buffer);
		String type = self.getStringBlankAsNull("type");
		
		//创建数组
		Object array = null;
		if(type != null) {
			//基本类型
			if("byte".equals(type)) {
				array = new byte[length];
			}else if("char".equals(type)) {
				array = new char[length];
			}else if("short".equals(type)) {
				array = new short[length];
			}else if("boolean".equals(type)) {
				array = new boolean[length];
			}else if("int".equals(type)) {
				array = new int[length];
			}else if("long".equals(type)) {
				array = new long[length];
			}else if("float".equals(type)) {
				array = new float[length];
			}else if("byte[]".equals(type)) {
				array = new byte[length];
			}else if("double".equals(type)) {
				array = new double[length];
			}
		}
		if(array == null) {
			//首先找creator
			String creator = self.getStringBlankAsNull("creator");			
			if(creator != null) {
				Object actionObj = actionContext.get("creator");
				if(actionObj != null && actionObj instanceof Action) {
					array = ((Action) actionObj).run(actionContext, "length", length);
				}
			}
		}
		
		if(array == null) {
			//使用createArray方法创建
			array = self.doAction("createArray", actionContext, "length", length);
		}

		for(int i=0; i<length; i++) {
			Object obj =  null;
			if(type != null) {
				obj = new HashMap<String, Object>();
			}else {
				obj = self.doAction("createObject", actionContext);
			}
			actionContext.peek().put("object", obj);
			
			for(Thing child : self.getChilds("Codec")) {
				child.doAction("encode", actionContext);				
			}
			
			if(type != null) {
				Array.set(array, i, ((Map<String, Object>) obj).get("value"));
			}else {
				Array.set(array, i, obj);
			}
		}
		
		CodecUtils.setValue(self, array, actionContext); 
	}
	
	public static Object[] createArray(ActionContext actionContext) {
		int length = actionContext.getObject("length");
		
		return new Object[length];
	}
}
