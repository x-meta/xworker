package test;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public class Test {
	public static long add(int x, int y){
		return x + y;
	}
	
	
	public static Object run(ActionContext actionContext){
		Thing self = actionContext.getObject("self");
		
		int x = self.doAction("getX", actionContext);
		int y = self.doAction("getY", actionContext);
		return add(x, y);
	}
	
}
