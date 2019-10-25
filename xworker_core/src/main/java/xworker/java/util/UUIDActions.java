package xworker.java.util;

import java.util.UUID;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.util.UtilData;

public class UUIDActions {
	public static Object randomUUID(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		UUID uuid = UUID.randomUUID();
		if(UtilData.isTrue(self.doAction("isToString", actionContext))) {
			return uuid.toString();
		}else {
			return uuid;
		}
	}
	
	public static Object nameUUIDFromBytes(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		
		byte[] bytes = self.doAction("getNameBytes", actionContext);
		UUID uuid = UUID.nameUUIDFromBytes(bytes);
		
		if(UtilData.isTrue(self.doAction("isToString", actionContext))) {
			return uuid.toString();
		}else {
			return uuid;
		}
	}
}
