package xworker.net.netty.buffer.codecs;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import ognl.OgnlException;
import xworker.util.UtilData;

public class IfCodec {
	public static void encode(ActionContext actionContext) throws OgnlException {
		Thing self = actionContext.getObject("self");
		
		if(UtilData.isTrue(self.doAction("getCondition", actionContext))) {
			for(Thing child : self.getChilds("Codec")) {
				child.doAction("encode", actionContext);				
			}
		}	
	}
	
	public static void decode(ActionContext actionContext) throws OgnlException {
		Thing self = actionContext.getObject("self");
				
		if(UtilData.isTrue(self.doAction("getCondition", actionContext))) {
			for(Thing child : self.getChilds("Codec")) {
				child.doAction("decode", actionContext);				
			}
		}		
	}
}
