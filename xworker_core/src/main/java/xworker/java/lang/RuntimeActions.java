package xworker.java.lang;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class RuntimeActions {
	public static void addShutdownHook(ActionContext actionContext){
		String key = "__xworker_java_lang_runtime_shutdownhook__";
		ShutdownHookThread th = (ShutdownHookThread) World.getInstance().getData(key);
		if(th == null){
			th = new ShutdownHookThread("xworker shutdown hook");
			World.getInstance().setData(key, th);
			
			Runtime.getRuntime().addShutdownHook(th);
		}
		th.addHook((Thing) actionContext.get("self"), actionContext) ;
	}
}
