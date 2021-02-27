package xworker.lang.executor;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

public interface ExecutorListener extends ExecutorService {
	/**
	 * 通知执行器某种信息。传入的thing应该实现create()方法用于创建界面。
	 * 
	 * @param message
	 * @param thing
	 * @param actionContext
	 */
	public void notify(String message, Thing thing, ActionContext actionContext);
}
