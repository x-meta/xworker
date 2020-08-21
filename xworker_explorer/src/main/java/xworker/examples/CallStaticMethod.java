package xworker.examples;

import org.xmeta.ActionContext;

public class CallStaticMethod {
	public static Object run(ActionContext actionContext) {
		String message = actionContext.getObject("message");
		System.out.println(message);
		
		return message;		
	}
}
