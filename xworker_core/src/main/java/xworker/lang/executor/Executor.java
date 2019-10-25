package xworker.lang.executor;

import java.util.Stack;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.executor.services.Log4jService;

/**
 * 执行事物模型的执行器，提供日志和UI请求等服务。
 * 
 * @author zyx
 *
 */
public class Executor {
	public final static byte TRACE = 0;
	public final static byte DEBUG = 1;
	public final static byte INFO = 2;
	public final static byte WARN = 3;
	public final static byte ERROR = 4;
	
	/** 执行服务 */
	private static ThreadLocal<Stack<ExecutorService>> executorServices = new ThreadLocal<Stack<ExecutorService>>();
	
	private static ExecutorService defaultExecutorService = new Log4jService();
	
	/**
	 * 设置默认的Executor服务。
	 * 
	 * @param defaultExecutorService
	 */
	public static void setDefaultExecutorService(ExecutorService defaultExecutorService) {
		Executor.defaultExecutorService = defaultExecutorService;
	}
	
	public static void trace(String TAG, String message) {
		ExecutorService service = getExecutorService();
		if(service != null) {
			service.trace(TAG, message);
		}
	}
	
	public static void trace(String TAG, String message, Throwable t) {
		ExecutorService service = getExecutorService();
		if(service != null) {
			service.trace(TAG, message, t);
		}
	}
	
	public static void trace(String TAG, String format, Object arg) {
		ExecutorService service = getExecutorService();
		if(service != null) {
			service.trace(TAG, format, arg);
		}
	}
	
	public static void trace(String TAG, String format, Object arg1, Object arg2) {
		ExecutorService service = getExecutorService();
		if(service != null) {
			service.trace(TAG, format, arg1, arg2);
		}
	}
	
	public static void setLogLevel(byte level) {
		ExecutorService service = getExecutorService();
		if(service != null) {
			service.setLogLevel(level);
		}
	}
	
	public static byte getLogLevel() {
		ExecutorService service = getExecutorService();
		if(service != null) {
			return service.getLogLevel();
		}else {
			return Executor.INFO;
		}
	}
	
	public static void trace(String TAG, String format, Object ...arguments) {
		ExecutorService service = getExecutorService();
		if(service != null) {
			service.trace(TAG, format, arguments);
		}
	}
	
	public static void debug(String TAG, String message) {
		ExecutorService service = getExecutorService();
		if(service != null) {
			service.debug(TAG, message);
		}
	}
	
	public static void debug(String TAG, String message, Throwable t) {
		ExecutorService service = getExecutorService();
		if(service != null) {
			service.trace(TAG, message, t);
		}
	}
	
	public static void debug(String TAG, String format, Object arg) {
		ExecutorService service = getExecutorService();
		if(service != null) {
			service.debug(TAG, format, arg);
		}
	}
	
	public static void debug(String TAG, String format, Object arg1, Object arg2) {
		ExecutorService service = getExecutorService();
		if(service != null) {
			service.debug(TAG, format, arg1, arg2);
		}
	}
	
	public static void debug(String TAG, String format, Object ...arguments) {
		ExecutorService service = getExecutorService();
		if(service != null) {
			service.debug(TAG, format, arguments);
		}
	}
	
	public static void info(String TAG, String message) {
		ExecutorService service = getExecutorService();
		if(service != null) {
			service.info(TAG, message);
		}
	}
	
	public static void info(String TAG, String message, Throwable t) {
		ExecutorService service = getExecutorService();
		if(service != null) {
			service.info(TAG, message, t);
		}
	}
	
	public static void info(String TAG, String format, Object arg) {
		ExecutorService service = getExecutorService();
		if(service != null) {
			service.info(TAG, format, arg);
		}
	}
	
	public static void info(String TAG, String format, Object arg1, Object arg2) {
		ExecutorService service = getExecutorService();
		if(service != null) {
			service.info(TAG, format, arg1, arg2);
		}
	}
	
	public static void info(String TAG, String format, Object ...arguments) {
		ExecutorService service = getExecutorService();
		if(service != null) {
			service.info(TAG, format, arguments);
		}
	}
	
	public static void warn(String TAG, String message) {
		ExecutorService service = getExecutorService();
		if(service != null) {
			service.warn(TAG, message);
		}
	}
	
	public static void warn(String TAG, String message, Throwable t) {
		ExecutorService service = getExecutorService();
		if(service != null) {
			service.warn(TAG, message, t);
		}
	}
	
	public static void warn(String TAG, String format, Object arg) {
		ExecutorService service = getExecutorService();
		if(service != null) {
			service.warn(TAG, format, arg);
		}
	}
	
	public static void warn(String TAG, String format, Object arg1, Object arg2) {
		ExecutorService service = getExecutorService();
		if(service != null) {
			service.warn(TAG, format, arg1, arg2);
		}
	}
	
	public static void warn(String TAG, String format, Object ...arguments) {
		ExecutorService service = getExecutorService();
		if(service != null) {
			service.warn(TAG, format, arguments);
		}
	}
	
	public static void error(String TAG, String message) {
		ExecutorService service = getExecutorService();
		if(service != null) {
			service.error(TAG, message);
		}
	}
	
	public static void error(String TAG, String message, Throwable t) {
		ExecutorService service = getExecutorService();
		if(service != null) {
			service.error(TAG, message, t);
		}
	}
	
	public static void error(String TAG, String format, Object arg) {
		ExecutorService service = getExecutorService();
		if(service != null) {
			service.error(TAG, format, arg);
		}
	}
	
	public static void error(String TAG, String format, Object arg1, Object arg2) {
		ExecutorService service = getExecutorService();
		if(service != null) {
			service.error(TAG, format, arg1, arg2);
		}
	}
	
	public static void error(String TAG, String format, Object ...arguments) {
		ExecutorService service = getExecutorService();
		if(service != null) {
			service.error(TAG, format, arguments);
		}
	}
	
	public static void requestUI(Thing request, ActionContext actionContext) {
		ExecutorService service = getExecutorService();
		if(service != null) {
			service.requestUI(request, actionContext);
		}
	}
	
	/**
	 * 返回ExecutorService所在的线程。
	 * 
	 * 在SWT下等UI在一个线程里时使用。如果UI请求是同步的，且请求线程和SWT是一个线程，同步会造成UI无响应等。
	 * @return
	 */
	public static Thread getExecutorServiceThread() {
		ExecutorService service = getExecutorService();
		if(service != null) {
			return service.getThread();
		}else {
			return null;
		}
	}
	
	/**
	 * 返回当前线程的顶端的执行服务。
	 * 
	 * @return
	 */
	public static ExecutorService getExecutorService() {
		Stack<ExecutorService> stack = getExecutorServiceStack();
		
		return stack.peek();
	}
	
	private static Stack<ExecutorService> getExecutorServiceStack() {
		Stack<ExecutorService> stack = executorServices.get();
		if(stack == null) {
			stack = new Stack<ExecutorService>();
			stack.push(defaultExecutorService);
			executorServices.set(stack);
		}
		
		return stack;
	}
	
	/**
	 * push和pop应该是成对出现的，使用try/fianlly。
	 * 
	 * @param service
	 */
	public static void push(ExecutorService service) {
		Stack<ExecutorService> stack = getExecutorServiceStack();
		stack.push(service);
	}
	
	public static void pop() {
		Stack<ExecutorService> stack = getExecutorServiceStack();
		stack.pop();
	}
	
	public static void print(Object message) {
		ExecutorService service = getExecutorService();
		if(service != null) {
			service.print(message);
		}
	}

	public static void println(Object message) {
		ExecutorService service = getExecutorService();
		if(service != null) {
			service.println(message);
		}
	}

	public static void errPrint(Object message) {
		ExecutorService service = getExecutorService();
		if(service != null) {
			service.errPrint(message);
		}
	}

	public static void errPrintln(Object message) {
		ExecutorService service = getExecutorService();
		if(service != null) {
			service.errPrintln(message);
		}
	}
	
	public static void removeRequest(Thing request, ActionContext actionContext) {
		ExecutorService service = getExecutorService();
		if(service != null) {
			service.removeRequest(request, actionContext);
		}
	}
	
}
