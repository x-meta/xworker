package xworker.lang.executor;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import java.util.List;

/**
 * <p>执行器服务的接口。</p>
 * 
 * <p>对于日志输出，Executor已判断了当前日志是否可以输出，因此对于ExecutorService来说日志都是强制输出的。
 * 比如使用Log4j输出日志，Log4j是INFO，而调用了ExecutorService的trace输出日志，此时需要把trace提升到info级别，
 * 保证日志可以输出。</p>
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
	 * 发出一个请求UI。
	 * 
	 */
	public Request requestUI(Thing thing, ActionContext actionContext);

	/**
	 * 返回当前所有UI请求列表。
	 * 
	 * @return UI请求列表
	 */
	public List<Request> getRequests();
	
	/**
	 * 移除UI请求。
	 * 
	 * @param request 要溢出UI请求
	 */
	public void removeRequest(Request request);
	
	/**
	 * 返回ExecutorService所在的线程。
	 * 
	 * 像SWT等UI在一个线程里是才需要返回，其它无线程限制的返回null。
	 * 
	 * @return 返回线程
	 */
	public Thread getThread();

	/**
	 * 是否支持打印日志。
	 *
	 * @return 是否支持打印日志，包括print相关
	 */
	public boolean isSupportLog();

	/**
	 * 是否支持UI请求。
	 *
	 * @return 是否支持UI请求
	 */
	public boolean isSupportRequest();
}
