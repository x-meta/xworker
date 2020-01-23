package xworker.lang.executor;

/**
 * UI请求的处理器。
 * 
 * @author zyx
 *
 */
public interface UIHandler {
	public void handleUIRequest(ExecuteRequest request); 
	
	public void removeRequest(ExecuteRequest request) ;
	
	public Thread getThread();
	
	public void setExecutorService(ExecutorService executorService);
}
