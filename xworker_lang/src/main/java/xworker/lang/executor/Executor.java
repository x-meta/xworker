package xworker.lang.executor;

import java.util.*;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.executor.services.JavaLoggingService;

/**
 * 执行事物模型的执行器，提供日志和UI请求等服务。
 * 
 * @author zyx
 *
 */
public class Executor {
	private static final String TAG = Executor.class.getName();
	public final static byte TRACE = 1;
	public final static byte DEBUG = 2;
	public final static byte INFO = 3;
	public final static byte WARN = 4;
	public final static byte ERROR = 5;
	
	/** 执行服务 */
	private static final ThreadLocal<Stack<ExecutorService>> executorServices = new ThreadLocal<>();
	
	private static List<ExecutorService> defaultExecutorServices = new ArrayList<>();
	
	/** tag设置的日志级别 */
	private static final Map<String, Byte> tagLogLevels = new HashMap<>();
	
	/** 执行器监听器，监听所有的操作，外加一个notify。注意处理的性能不要影响其它程序。 */
	private static ExecutorListener listener = null;
	
	static {
		try {
			defaultExecutorServices.add(DefaultRequestService.getInstance());

			defaultExecutorServices.add(new JavaLoggingService());
			Class.forName("xworker.lang.executor.services.Log4jService");
		}catch(Throwable ignored) {
		}
	}
	
	/**
	 * 设置默认的Executor服务。
	 *
	 * @param defaultExecutorService 默认执行服务
	 */
	public static void addDefaultExecutorService(ExecutorService defaultExecutorService) {
		if(defaultExecutorService != null && !Executor.defaultExecutorServices.contains(defaultExecutorService)) {
			Executor.defaultExecutorServices.add(defaultExecutorService);
		}
	}

	public static void removeDefaultExecutorService(ExecutorService executorService){
		defaultExecutorServices.remove(executorService);
	}
	
	public static void setTagLogLevel(String tag, byte level) {
		tagLogLevels.put(tag, level);
	}
	
	public static void removeTagLevel(String tag) {
		tagLogLevels.remove(tag);
	}
	
	public static Map<String, Byte> getTagLogLevels(){
		return tagLogLevels;
	}
	
	private static byte getTagLogLevel(String tag, byte defaultLevel) {
		if(tag == null || "".equals(tag)) {
			return defaultLevel;
		}
		
		Byte level = tagLogLevels.get(tag);
		if(level != null) {
			return level;
		}else {
			int index = tag.lastIndexOf(".");
			if(index != -1) {
				return getTagLogLevel(tag.substring(0, index), defaultLevel);
			}else {			
				return defaultLevel;
			}
		}
	}
	
	public static void trace(String TAG, String message) {
		if(listener != null) {
			listener.trace(TAG, message);
		}

		ExecutorService service = getLogExecutorService();
		if(service != null && Executor.isLogLevelEnabled(service, TAG, Executor.TRACE)) {
			service.trace(TAG, message);
		}
	}
	
	public static void trace(String TAG, String message, Throwable t) {
		if(listener != null) {
			listener.trace(TAG, message, t);
		}
		
		ExecutorService service = getLogExecutorService();
		if(service != null && Executor.isLogLevelEnabled(service, TAG, Executor.TRACE)) {
			service.trace(TAG, message, t);
		}
	}
	
	public static void trace(String TAG, String format, Object arg) {
		if(listener != null) {
			listener.trace(TAG, format, arg);
		}
		
		ExecutorService service = getLogExecutorService();
		if(service != null && Executor.isLogLevelEnabled(service, TAG, Executor.TRACE)) {
			service.trace(TAG, format, arg);
		}
	}
	
	public static void trace(String TAG, String format, Object arg1, Object arg2) {
		if(listener != null) {
			listener.trace(TAG, format, arg1, arg2);
		}
		
		ExecutorService service = getLogExecutorService();
		if(service != null && Executor.isLogLevelEnabled(service, TAG, Executor.TRACE)) {
			service.trace(TAG, format, arg1, arg2);
		}
	}
	
	/**
	 * <p>设置Executor当前的ExecutorService的日志级别。</p>
	 * 
	 * 可能并不会改变系统的日志级别。比如XWorker用于把日志输出到Log4j的ExecutorService，它不会改变Log4j的日志级别，
	 * 而是把低级的日志输入到高级中。比如Executor的日志级别是debug，而log4j是info，那么会把Executor的debug日志
	 * 输出到Log4j的info级别中。
	 * 
	 * @param level 日志级别
	 */
	public static void setLogLevel(byte level) {
		ExecutorService service = getLogExecutorService();
		if(service != null) {
			service.setLogLevel(level);
		}
	}
	
	/**
	 * 返回是否指定的日志级别是激活的。
	 * 
	 * @param level 日志级别
	 * @return 是否会打印
	 */
	public static boolean isLogLevelEnabled(String TAG, byte level) {
		return level >= getTagLogLevel(TAG, getLogLevel());
	}

	public static boolean isLogLevelEnabled(ExecutorService executorService, String TAG, byte level){
		return level >= getTagLogLevel(TAG, executorService.getLogLevel());
	}
	
	/**
	 * 返回Executor当前的ExecutorService的日志级别。
	 * 
	 * @return 当前日志级别
	 */
	public static byte getLogLevel() {
		ExecutorService service = getLogExecutorService();
		if(service != null) {
			return service.getLogLevel();
		}else {
			return Executor.INFO;
		}
	}
	
	public static void trace(String TAG, String format, Object ...arguments) {
		if(listener != null) {
			listener.trace(TAG, format, arguments);
		}
		
		ExecutorService service = getLogExecutorService();
		if(service != null && Executor.isLogLevelEnabled(service, TAG, Executor.TRACE)) {
			service.trace(TAG, format, arguments);
		}
	}
	
	public static void debug(String TAG, String message) {
		if(listener != null) {
			listener.debug(TAG, message);
		}

		ExecutorService service = getLogExecutorService();
		if(service != null && Executor.isLogLevelEnabled(service, TAG, Executor.DEBUG)) {
			service.debug(TAG, message);
		}
	}
	
	public static void debug(String TAG, String message, Throwable t) {
		if(listener != null) {
			listener.debug(TAG, message, t);
		}		

		ExecutorService service = getLogExecutorService();
		if(service != null && Executor.isLogLevelEnabled(service, TAG, Executor.DEBUG)) {
			service.trace(TAG, message, t);
		}
	}
	
	public static void debug(String TAG, String format, Object arg) {
		if(listener != null) {
			listener.debug(TAG, format, arg);
		}

		ExecutorService service = getLogExecutorService();
		if(service != null && Executor.isLogLevelEnabled(service, TAG, Executor.DEBUG)) {
			service.debug(TAG, format, arg);
		}
	}
	
	public static void debug(String TAG, String format, Object arg1, Object arg2) {
		if(listener != null) {
			listener.debug(TAG, format, arg1, arg2);
		}		
		
		ExecutorService service = getLogExecutorService();
		if(service != null && Executor.isLogLevelEnabled(service, TAG, Executor.DEBUG)) {
			service.debug(TAG, format, arg1, arg2);
		}
	}
	
	public static void debug(String TAG, String format, Object ...arguments) {
		if(listener != null) {
			listener.debug(TAG, format, arguments);
		}

		ExecutorService service = getLogExecutorService();
		if(service != null && Executor.isLogLevelEnabled(service, TAG, Executor.DEBUG)) {
			service.debug(TAG, format, arguments);
		}
	}
	
	public static void info(String TAG, String message) {
		if(listener != null) {
			listener.info(TAG, message);
		}		
		
		ExecutorService service = getLogExecutorService();
		if(service != null && Executor.isLogLevelEnabled(service, TAG, Executor.INFO)) {
			service.info(TAG, message);
		}
	}
	
	public static void info(String TAG, String message, Throwable t) {
		if(listener != null) {
			listener.info(TAG, message, t);
		}
		
		ExecutorService service = getLogExecutorService();
		if(service != null && Executor.isLogLevelEnabled(service, TAG, Executor.INFO)) {
			service.info(TAG, message, t);
		}
	}
	
	public static void info(String TAG, String format, Object arg) {
		if(listener != null) {
			listener.info(TAG, format, arg);
		}
		
		ExecutorService service = getLogExecutorService();
		if(service != null && Executor.isLogLevelEnabled(service, TAG, Executor.INFO)) {
			service.info(TAG, format, arg);
		}
	}
	
	public static void info(String TAG, String format, Object arg1, Object arg2) {
		if(listener != null) {
			listener.info(TAG, format, arg1, arg2);
		}
		
		ExecutorService service = getLogExecutorService();
		if(service != null && Executor.isLogLevelEnabled(service, TAG, Executor.INFO)) {
			service.info(TAG, format, arg1, arg2);
		}
	}
	
	public static void info(String TAG, String format, Object ...arguments) {
		if(listener != null) {
			listener.info(TAG, format, arguments);
		}
		
		ExecutorService service = getLogExecutorService();
		if(service != null && Executor.isLogLevelEnabled(service, TAG, Executor.INFO)) {
			service.info(TAG, format, arguments);
		}
	}
	
	public static void warn(String TAG, String message) {
		if(listener != null) {
			listener.warn(TAG, message);
		}
		
		ExecutorService service = getLogExecutorService();
		if(service != null && Executor.isLogLevelEnabled(service, TAG, Executor.WARN)) {
			service.warn(TAG, message);
		}
	}
	
	public static void warn(String TAG, String message, Throwable t) {
		if(listener != null) {
			listener.warn(TAG, message, t);
		}
		
		ExecutorService service = getLogExecutorService();
		if(service != null && Executor.isLogLevelEnabled(service, TAG, Executor.WARN)) {
			service.warn(TAG, message, t);
		}
	}
	
	public static void warn(String TAG, String format, Object arg) {
		if(listener != null) {
			listener.warn(TAG, format, arg);
		}
		
		ExecutorService service = getLogExecutorService();
		if(service != null && Executor.isLogLevelEnabled(service, TAG, Executor.WARN)) {
			service.warn(TAG, format, arg);
		}
	}
	
	public static void warn(String TAG, String format, Object arg1, Object arg2) {
		if(listener != null) {
			listener.warn(TAG, format, arg1, arg2);
		}
		
		ExecutorService service = getLogExecutorService();
		if(service != null && Executor.isLogLevelEnabled(service, TAG, Executor.WARN)) {
			service.warn(TAG, format, arg1, arg2);
		}
	}
	
	public static void warn(String TAG, String format, Object ...arguments) {
		if(listener != null) {
			listener.warn(TAG, format, arguments);
		}
		
		ExecutorService service = getLogExecutorService();
		if(service != null && Executor.isLogLevelEnabled(service, TAG, Executor.WARN)) {
			service.warn(TAG, format, arguments);
		}
	}
	
	public static void error(String TAG, String message) {
		if(listener != null) {
			listener.error(TAG, message);
		}
		
		ExecutorService service = getLogExecutorService();
		if(service != null && Executor.isLogLevelEnabled(service, TAG, Executor.ERROR)) {
			service.error(TAG, message);
		}
	}
	
	public static void error(String TAG, String message, Throwable t) {
		if(listener != null) {
			listener.error(TAG, message, t);
		}
		
		ExecutorService service = getLogExecutorService();
		if(service != null && Executor.isLogLevelEnabled(service, TAG, Executor.ERROR)) {
			service.error(TAG, message, t);
		}
	}
	
	public static void error(String TAG, String format, Object arg) {
		if(listener != null) {
			listener.error(TAG, format, arg);
		}
		
		ExecutorService service = getLogExecutorService();
		if(service != null && Executor.isLogLevelEnabled(service, TAG, Executor.ERROR)) {
			service.error(TAG, format, arg);
		}
	}
	
	public static void error(String TAG, String format, Object arg1, Object arg2) {
		if(listener != null) {
			listener.error(TAG, format, arg1, arg2);
		}
		
		ExecutorService service = getLogExecutorService();
		if(service != null && Executor.isLogLevelEnabled(service, TAG, Executor.ERROR)) {
			service.error(TAG, format, arg1, arg2);
		}
	}
	
	public static void error(String TAG, String format, Object ...arguments) {
		if(listener != null) {
			listener.error(TAG, format, arguments);
		}
		
		ExecutorService service = getLogExecutorService();
		if(service != null && Executor.isLogLevelEnabled(service, TAG, Executor.ERROR)) {
			service.error(TAG, format, arguments);
		}
	}
	
	/**
	 * 请求UI。request模型没有明确的定义，比如在SWT下一般要实现createSWT方法，但在其它环境下就没有明确定义了。
	 * 可以参看动作模型：xworker.lang.executor.ExecutorActions/@RequestUI。
	 * 
	 * @param request
	 * @param actionContext
	 */
	public static Request requestUI(Thing request, ActionContext actionContext) {
		if(listener != null) {
			listener.requestUI(request, actionContext);
		}
		
		ExecutorService service = getRequestExecutorService();
		if(service != null) {
			Request request1 = service.requestUI(request, actionContext);
			if(request1 != null && listener != null){
				listener.requestUI(request1);
			}
		}

		return null;
	}
	
	/**
	 * 返回ExecutorService所在的线程。
	 * 
	 * 在SWT下等UI在一个线程里时使用。如果UI请求是同步的，且请求线程和SWT是一个线程，同步会造成UI无响应等。
	 * @return
	 */
	public static Thread getExecutorServiceThread() {
		ExecutorService service = getRequestExecutorService();
		if(service != null) {
			return service.getThread();
		}else {
			return null;
		}
	}

	public static ExecutorService getRequestExecutorService(){
		Stack<ExecutorService> stack = getExecutorServiceStack();
		for(int i=stack.size()-1; i>=0; i--) {
			ExecutorService es = stack.get(i);
			if(es.isSupportRequest()){
				return es;
			}
		}

		for(int i=defaultExecutorServices.size() -1 ; i>=0; i--){
			ExecutorService executorService = defaultExecutorServices.get(i);
			if(executorService.isSupportRequest()){
				return executorService;
			}
		}

		return null;
	}

	public static ExecutorService getLogExecutorService(){
		Stack<ExecutorService> stack = getExecutorServiceStack();
		for(int i=stack.size()-1; i>=0; i--) {
			ExecutorService es = stack.get(i);
			if(es.isSupportLog()){
				return es;
			}
		}

		for(int i=defaultExecutorServices.size() -1 ; i>=0; i--){
			ExecutorService executorService = defaultExecutorServices.get(i);
			if(executorService.isSupportLog()){
				return executorService;
			}
		}
		return null;
	}

	public static List<ExecutorService> getExecutorServices(){
		Stack<ExecutorService> stack = getExecutorServiceStack();
		return new ArrayList<>(stack);
	}

	public static void push(List<ExecutorService> executorServices){
		Stack<ExecutorService> stack = getExecutorServiceStack();
		stack.addAll(executorServices);
	}

	public static List<ExecutorService> getDefaultExecutorServices(){
		return defaultExecutorServices;
	}

	/**
	 * 获取紧邻current但比current更早压入栈的ExecutorService，如果不存在返回null。
	 * 
	 * @param current
	 * @return
	 */
	public static ExecutorService getParentExecutorService(ExecutorService current) {
		Stack<ExecutorService> stack = getExecutorServiceStack();
		ExecutorService parrent = null;
		for(int i=0; i<stack.size(); i++) {
			ExecutorService es = stack.get(i);
			if(es != current) {
				parrent = es;
			}else {
				return parrent;
			}
		}
		
		return null;
	}
	
	private static Stack<ExecutorService> getExecutorServiceStack() {
		Stack<ExecutorService> stack = executorServices.get();
		if(stack == null) {
			stack = new Stack<>();
			for(ExecutorService es : defaultExecutorServices) {
				stack.push(es);
			}
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
		if(listener != null) {
			listener.print(message);
		}
		
		ExecutorService service = getLogExecutorService();
		if(service != null) {
			service.print(message);
		}
	}

	public static void println(Object message) {
		if(listener != null) {
			listener.println(message);
		}
		
		ExecutorService service = getLogExecutorService();
		if(service != null) {
			service.println(message);
		}
	}

	public static void errPrint(Object message) {
		if(listener != null) {
			listener.errPrint(message);
		}
		
		ExecutorService service = getLogExecutorService();
		if(service != null) {
			service.errPrint(message);
		}
	}

	public static void errPrintln(Object message) {
		if(listener != null) {
			listener.errPrintln(message);
		}

		ExecutorService service = getLogExecutorService();
		if(service != null) {
			service.errPrintln(message);
		}
	}
	
	public static void removeRequest(Request request) {
		ExecutorService service = getLogExecutorService();
		if(service != null) {
			service.removeRequest(request);
		}
	}
	
	/**
	 * 启动一个线程执行Runnable，并把当前的ExecutorService带过去。
	 * 
	 * @param runnable 要执行的任务
	 */
	public static void startThread(final Runnable runnable) {
		final List<ExecutorService> executorServices = Executor.getExecutorServices();
		new Thread(new Runnable() {
			public void run() {
				try {
					Executor.push(executorServices);
					
					runnable.run();
				}finally {
					Executor.pop();
				}
			}
		}).start();
	}
	
	public static ExecutorListener getListener() {
		return listener;
	}
	
	public static void setListener(ExecutorListener listener) {
		Executor.listener = listener;
	}
	
	public static void notify(String message, Thing thing, ActionContext actionContext) {
		if(listener != null) {
			listener.notify(message, thing, actionContext);
		}
	}
}
