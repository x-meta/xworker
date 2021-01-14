package xworker.org.apache.kafka;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class KafkaConfig {
	static private boolean inited = false;
	
	/**
	 * 检查是否已经初始化环境，如果没有则初始化。初始化包括根据依赖加载和创建新的ClassLoader。
	 */
	public synchronized static void checkInit() {
		if(inited) {
			return;
		}
		
		Thing config = World.getInstance().getThing("xworker.org.apache.kafka.KafkaClassLoader");
		config.doAction("run", new ActionContext());
		inited = true;
	}
	
	public static void init(ActionContext actionContext) {
		checkInit();
	}
}
