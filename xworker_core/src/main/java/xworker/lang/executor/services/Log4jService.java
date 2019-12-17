package xworker.lang.executor.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.lang.executor.Executor;
import xworker.lang.executor.ExecutorService;

public class Log4jService implements ExecutorService{
	byte level = Executor.INFO;
	static final String FQCN = Executor.class.getName();
	
	private void log(byte level, String TAG, String message) {
		LocationAwareLogger logger = (LocationAwareLogger) LoggerFactory.getLogger(TAG);
		byte realLevel = getLevel(logger, level);
		switch(realLevel) {
		case -1:
			return;  //不打印日志
		case Executor.TRACE:
			logger.log(null, FQCN, LocationAwareLogger.TRACE_INT, message, null, null);			
			break;
		case Executor.DEBUG:
			logger.log(null, FQCN, LocationAwareLogger.DEBUG_INT, message, null, null);
			break;
		case Executor.INFO:
			logger.log(null, FQCN, LocationAwareLogger.INFO_INT, message, null, null);
			break;
		case Executor.WARN:
			logger.log(null, FQCN, LocationAwareLogger.WARN_INT, message, null, null);
			break;
		case Executor.ERROR:
			logger.log(null, FQCN, LocationAwareLogger.ERROR_INT, message, null, null);
			break;
		}
	}
	
	private void log(byte level, String TAG, String message, Throwable t) {
		LocationAwareLogger logger = (LocationAwareLogger) LoggerFactory.getLogger(TAG);
		byte realLevel = getLevel(logger, level);
		switch(realLevel) {
		case -1:
			return;  //不打印日志
		case Executor.TRACE:
			logger.log(null, FQCN, LocationAwareLogger.TRACE_INT, message, null, t);
			break;
		case Executor.DEBUG:
			logger.log(null, FQCN, LocationAwareLogger.DEBUG_INT, message, null, t);
			break;
		case Executor.INFO:
			logger.log(null, FQCN, LocationAwareLogger.INFO_INT, message, null, t);
			break;
		case Executor.WARN:
			logger.log(null, FQCN, LocationAwareLogger.WARN_INT, message, null, t);
			break;
		case Executor.ERROR:
			logger.log(null, FQCN, LocationAwareLogger.ERROR_INT, message, null, t);
			break;
		}
	}
	
	private void log(byte level, String TAG, String format, Object arg) {
		LocationAwareLogger logger = (LocationAwareLogger) LoggerFactory.getLogger(TAG);
		byte realLevel = getLevel(logger, level);
		switch(realLevel) {
		case -1:
			return;  //不打印日志
		case Executor.TRACE:
			logger.log(null, FQCN, LocationAwareLogger.TRACE_INT, format, new Object[] {arg}, null);
			break;
		case Executor.DEBUG:
			logger.log(null, FQCN, LocationAwareLogger.DEBUG_INT, format, new Object[] {arg}, null);
			break;
		case Executor.INFO:
			logger.log(null, FQCN, LocationAwareLogger.INFO_INT, format, new Object[] {arg}, null);
			break;
		case Executor.WARN:
			logger.log(null, FQCN, LocationAwareLogger.WARN_INT, format, new Object[] {arg}, null);
			break;
		case Executor.ERROR:
			logger.log(null, FQCN, LocationAwareLogger.ERROR_INT, format, new Object[] {arg}, null);
			break;
		}
	}
	
	private void log(byte level, String TAG, String format, Object arg1, Object arg2) {
		LocationAwareLogger logger = (LocationAwareLogger) LoggerFactory.getLogger(TAG);
		byte realLevel = getLevel(logger, level);
		switch(realLevel) {
		case -1:
			return;  //不打印日志
		case Executor.TRACE:
			logger.log(null, FQCN, LocationAwareLogger.TRACE_INT, format, new Object[] {arg1, arg2}, null);
			break;
		case Executor.DEBUG:
			logger.log(null, FQCN, LocationAwareLogger.DEBUG_INT, format, new Object[] {arg1, arg2}, null);
			break;
		case Executor.INFO:
			logger.log(null, FQCN, LocationAwareLogger.INFO_INT, format, new Object[] {arg1, arg2}, null);
			break;
		case Executor.WARN:
			logger.log(null, FQCN, LocationAwareLogger.WARN_INT, format, new Object[] {arg1, arg2}, null);
			break;
		case Executor.ERROR:
			logger.log(null, FQCN, LocationAwareLogger.ERROR_INT, format, new Object[] {arg1, arg2}, null);
			break;
		}
	}
	
	private void log(byte level, String TAG, String format,  Object... arguments) {
		LocationAwareLogger logger = (LocationAwareLogger) LoggerFactory.getLogger(TAG);
		byte realLevel = getLevel(logger, level);
		switch(realLevel) {
		case -1:
			return;  //不打印日志
		case Executor.TRACE:
			logger.log(null, FQCN, LocationAwareLogger.TRACE_INT, format, arguments, null);
			break;
		case Executor.DEBUG:
			logger.log(null, FQCN, LocationAwareLogger.DEBUG_INT, format, arguments, null);
			break;
		case Executor.INFO:
			logger.log(null, FQCN, LocationAwareLogger.INFO_INT, format, arguments, null);
			break;
		case Executor.WARN:
			logger.log(null, FQCN, LocationAwareLogger.WARN_INT, format, arguments, null);
			break;
		case Executor.ERROR:
			logger.log(null, FQCN, LocationAwareLogger.ERROR_INT, format, arguments, null);
			break;
		}
	}
	
	/**
	 * 返回最后实际用的Level，如果是-1表示不可用。
	 * 
	 * @param logger
	 * @param level
	 */
	private byte getLevel(Logger logger, byte level) {
		if(level < this.level) {
			return -1;
		}
		
		switch(level) {
		case Executor.TRACE:
			if(logger.isTraceEnabled()) {
				return Executor.TRACE;
			}
		case Executor.DEBUG:
			if(logger.isDebugEnabled()) {
				return Executor.DEBUG;
			}
		case Executor.INFO:
			if(logger.isInfoEnabled()) {
				return Executor.INFO;
			}
		case Executor.WARN:
			if(logger.isWarnEnabled()) {
				return Executor.WARN;
			}
		case Executor.ERROR:
			if(logger.isErrorEnabled()) {
				return Executor.ERROR;
			}
		}

		return -1;		
	}
	
	@Override
	public void trace(String TAG, String message) {
		log(Executor.TRACE, TAG, message);
	}

	@Override
	public void trace(String TAG, String message, Throwable t) {
		log(Executor.TRACE, TAG, message, t);
	}

	@Override
	public void trace(String TAG, String format, Object arg) {
		log(Executor.TRACE, TAG, format, arg);		
	}

	@Override
	public void trace(String TAG, String format, Object arg1, Object arg2) {
		log(Executor.TRACE, TAG, format, arg1, arg2);		
	}

	@Override
	public void trace(String TAG, String format, Object... arguments) {
		log(Executor.TRACE, TAG, format, arguments);		
	}

	@Override
	public void debug(String TAG, String message) {
		log(Executor.DEBUG, TAG, message);
	}

	@Override
	public void debug(String TAG, String message, Throwable t) {
		log(Executor.DEBUG, TAG, message, t);
	}

	@Override
	public void debug(String TAG, String format, Object arg) {
		log(Executor.DEBUG, TAG, format, arg);	
	}

	@Override
	public void debug(String TAG, String format, Object arg1, Object arg2) {
		log(Executor.DEBUG, TAG, format, arg1, arg2);	
	}

	@Override
	public void debug(String TAG, String format, Object... arguments) {
		log(Executor.DEBUG, TAG, format, arguments);	
	}
	
	@Override
	public void info(String TAG, String message) {
		log(Executor.INFO, TAG, message);
	}

	@Override
	public void info(String TAG, String message, Throwable t) {
		log(Executor.INFO, TAG, message, t);
	}

	@Override
	public void info(String TAG, String format, Object arg) {
		log(Executor.INFO, TAG, format, arg);	
	}

	@Override
	public void info(String TAG, String format, Object arg1, Object arg2) {
		log(Executor.INFO, TAG, format, arg1, arg2);	
	}

	@Override
	public void info(String TAG, String format, Object... arguments) {
		log(Executor.INFO, TAG, format, arguments);	
	}
	
	@Override
	public void warn(String TAG, String message) {
		log(Executor.WARN, TAG, message);
	}

	@Override
	public void warn(String TAG, String message, Throwable t) {
		log(Executor.WARN, TAG, message, t);
	}

	@Override
	public void warn(String TAG, String format, Object arg) {
		log(Executor.WARN, TAG, format, arg);	
	}

	@Override
	public void warn(String TAG, String format, Object arg1, Object arg2) {
		log(Executor.WARN, TAG, format, arg1, arg2);	
	}

	@Override
	public void warn(String TAG, String format, Object... arguments) {
		log(Executor.WARN, TAG, format, arguments);	
	}
	
	@Override
	public void error(String TAG, String message) {
		log(Executor.ERROR, TAG, message);
	}

	@Override
	public void error(String TAG, String message, Throwable t) {
		log(Executor.ERROR, TAG, message, t);
	}

	@Override
	public void error(String TAG, String format, Object arg) {
		log(Executor.ERROR, TAG, format, arg);		
	}

	@Override
	public void error(String TAG, String format, Object arg1, Object arg2) {
		log(Executor.ERROR, TAG, format, arg1, arg2);	
	}

	@Override
	public void error(String TAG, String format, Object... arguments) {
		log(Executor.ERROR, TAG, format, arguments);		
	}

	@Override
	public void requestUI(Thing request, ActionContext actionContext) {
		request.doAction("cancel", actionContext);
	}

	@Override
	public Thread getThread() {
		return null;
	}

	@Override
	public void print(Object message) {
		System.out.print(message);
	}

	@Override
	public void println(Object message) {
		System.out.println(message);
	}

	@Override
	public void errPrint(Object message) {
		System.err.print(message);
	}

	@Override
	public void errPrintln(Object message) {
		System.err.println(message);
	}

	@Override
	public void removeRequest(Thing request, ActionContext actionContext) {
	}

	@Override
	public void setLogLevel(byte level) {
		this.level = level;
	}

	@Override
	public byte getLogLevel() {
		return level;
	}

}
