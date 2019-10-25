package xworker.swt.browser;

public interface BrowserCallback {
	public void evaluationFailed(Exception exception);
	
	public void evaluationSucceeded(Object result);
	
}
