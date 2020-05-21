package xworker.java.security;

import java.security.NoSuchAlgorithmException;

import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class MessageActions {
	public static Object messageMD5Salt(ActionContext actionContext) throws NoSuchAlgorithmException {
		Thing self = actionContext.getObject("self");
		
		byte[] message = self.doAction("getMessage", actionContext);
		byte[] salt = self.doAction("getSalt", actionContext);

		Action md5 = actionContext.getObject("md5");
		
		String result = md5.run(actionContext, "message", message);
		if(salt != null) {
			String saltResult = md5.run(actionContext, "message", salt);
			result = result + saltResult;
			result = md5.run(actionContext, "message", result);
		}
		
		return result;
	}
}
