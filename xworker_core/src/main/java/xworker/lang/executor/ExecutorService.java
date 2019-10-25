package xworker.lang.executor;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

/**
 * 执行器服务的接口。
 * 
 * @author zyx
 *
 */
public interface ExecutorService {	
	public void setLogLevel(byte level);
	public byte getLogLevel();
	
	public void trace(String TAG, String message);
	public void trace(String TAG, String message, Throwable t);
	public void trace(String TAG, String format, Object arg);
	public void trace(String TAG, String format, Object arg1, Object arg2);
	public void trace(String TAG, String format, Object ...arguments);
	
	public void debug(String TAG, String message);
	public void debug(String TAG, String message, Throwable t);
	public void debug(String TAG, String format, Object arg);
	public void debug(String TAG, String format, Object arg1, Object arg2);
	public void debug(String TAG, String format, Object ...arguments);
	
	public void info(String TAG, String message);
	public void info(String TAG, String message, Throwable t);
	public void info(String TAG, String format, Object arg);
	public void info(String TAG, String format, Object arg1, Object arg2);
	public void info(String TAG, String format, Object ...arguments);
	
	public void warn(String TAG, String message);
	public void warn(String TAG, String message, Throwable t);
	public void warn(String TAG, String format, Object arg);
	public void warn(String TAG, String format, Object arg1, Object arg2);
	public void warn(String TAG, String format, Object ...arguments);
	
	public void error(String TAG, String message);
	public void error(String TAG, String message, Throwable t);
	public void error(String TAG, String format, Object arg);
	public void error(String TAG, String format, Object arg1, Object arg2);
	public void error(String TAG, String format, Object ...arguments);
	
	public void print(Object message);
	public void println(Object message);
	public void errPrint(Object message);
	public void errPrintln(Object message);
	
	/**
	 * 处理UI请求，处理完毕后调用request的ok或cancel的方法。
	 * 
	 * @param request
	 * @param actionContext
	 */
	public void requestUI(Thing request, ActionContext actionContext);

	/**
	 * 移除UI请求。
	 * 
	 * @param request
	 */
	public void removeRequest(Thing request, ActionContext actionContext);
	
	/**
	 * 返回ExecutorService所在的线程。
	 * 
	 * 像SWT等UI在一个线程里是才需要返回，其它无线程限制的返回null。
	 * 
	 * @return
	 */
	public Thread getThread();
}
