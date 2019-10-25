package xworker.net.netty.buffer;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ByteBufCodec {
	public static void encode(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		for(Thing child : self.getChilds("Codec")) {
			child.doAction("encode", actionContext);
		}
	}
	
	public static void decode(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		for(Thing child : self.getChilds("Codec")) {
			child.doAction("decode", actionContext);
		}
	}

}
