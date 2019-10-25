package xworker.security;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public interface SecurityHandler {
	/**
	 * 处理安全问题。
	 * 
	 * @param path
	 * @param actionContext
	 */
	public boolean doCheck(String env, String permission, String action, String path, ActionContext actionContext);

	/**
	 * 返回定义该安全处理器的事物。
	 * 
	 * @return
	 */
	public Thing getThing();
}
