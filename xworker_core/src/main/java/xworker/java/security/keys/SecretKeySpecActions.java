package xworker.java.security.keys;

import javax.crypto.spec.SecretKeySpec;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class SecretKeySpecActions {
	public static SecretKeySpec create(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		String algorithm = self.doAction("getAlgorithm", actionContext);
		byte[] key = self.doAction("getKey", actionContext);
		
		return new SecretKeySpec(key, algorithm); 
	}
}
