package xworker.lang.executor;

import java.util.List;

/**
 * UI请求的处理器。
 * 
 * @author zyx
 *
 */
public interface UIHandler {
	public void handleUIRequest(Request request);
	
	public List<Request> getRequests();
	
	public void removeRequest(Request request) ;

	public Thread getThread();
	
	public void setExecutorService(ExecutorService executorService);
}
