package xworker.lang.executor;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

/**
 * UI请求的处理器。
 * 
 * @author zyx
 *
 */
public interface UIHandler {
	public void handleUIRequest(Thing request, ActionContext actionContext); 
	
	public void removeRequest(Thing request, ActionContext actionContext) ;
	
	public Thread getThread();
}
