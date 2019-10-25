package xworker.net.netty.buffer.codecs;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import io.netty.buffer.ByteBuf;
import ognl.OgnlException;
import xworker.util.UtilData;

public class ListCodec {
	public static void encode(ActionContext actionContext) throws OgnlException {
		Thing self = actionContext.getObject("self");
		ByteBuf buffer = actionContext.getObject("buffer");
		
		
		int lengthPrefix = self.doAction("getLengthPrefix", actionContext);
	    List<?> list = (List<?>) CodecUtils.getValue(self, actionContext);
	    String type = self.getStringBlankAsNull("type");
	    
		CodecUtils.putLength(self, lengthPrefix, list.size(), buffer);
		for(Object obj : list) {
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
		
		List<Object> list = self.doAction("createList", actionContext);
		if(list == null) {
			list = Collections.EMPTY_LIST;
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
				list.add(((Map<String, Object>) obj).get("value"));
			}else {
				list.add(obj);
			}
		}
		
		CodecUtils.setValue(self, list, actionContext); 
	}
}
