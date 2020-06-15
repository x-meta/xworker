package xworker.java.security;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class SecureRandomActions {
	public static SecureRandom create(ActionContext actionContext) throws NoSuchAlgorithmException {
		Thing self = actionContext.getObject("self");
		
		String algorithm = self.doAction("getAlgorithm", actionContext);
		return SecureRandom.getInstance(algorithm);
	}
}
