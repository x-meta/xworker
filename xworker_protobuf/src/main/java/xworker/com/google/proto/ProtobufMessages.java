package xworker.com.google.proto;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class ProtobufMessages {
	public static ProtobufMessageFactory getMessageFactory(final ActionContext actionContext) throws NoSuchMethodException, SecurityException {
		final Thing self = actionContext.getObject("self");
		
		String key = "__messageFactory__";
		ProtobufMessageFactory factory = self.getData(key);
		if(factory == null) {
			factory = new  ProtobufMessageFactory(self, actionContext);					
			self.setData(key, factory);
		}
		
		return factory;
	}
	
	public static void clearMessageFactory(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		String key = "__messageFactory__";
		self.setData(key, null);
	}
	
	public static Object createMessage(ActionContext actionContext) throws Exception {
		Thing self = actionContext.getObject("self");
		
		Thing root = self;
		while(!root.getThingName().equals("ProtobufMessages")) {
			root = root.getParent();
		}
		ProtobufMessageFactory factory = root.doAction("getMessageFactory", actionContext);
		
		Thing messageProto = self.doAction("getMessageProto", actionContext);
		String jsonData = self.doAction("getJsonData", actionContext);
		return factory.decode(messageProto.getMetadata().getName(), jsonData);
	}
	
	
}
