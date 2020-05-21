package xworker.lang.executor;

import java.util.List;

/**
 * UI请求的处理器。
 * 
 * @author zyx
 *
 */
public interface UIHandler {
	public void handleUIRequest(ExecuteRequest request); 
	
	public List<ExecuteRequest> getRequestUIs();
	
	public void removeRequest(ExecuteRequest request) ;
	
	public Thread getThread();
	
	public void setExecutorService(ExecutorService executorService);
}
