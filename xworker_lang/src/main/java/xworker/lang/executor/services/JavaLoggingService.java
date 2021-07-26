package xworker.lang.executor.services;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import xworker.lang.executor.Request;
import xworker.lang.executor.Executor;

public class JavaLoggingService extends AbstractLogService {
	@Override
	protected void log(byte level, String TAG, String msg) {
		Logger logger = Logger.getLogger(TAG);
		switch(level) {
			case Executor.TRACE:
				logger.log(Level.FINER, msg);
				break;
			case Executor.DEBUG:
				logger.log(Level.FINE, msg);
				break;
			case Executor.INFO:
				logger.log(Level.INFO, msg);
				break;
			case Executor.WARN:
				logger.log(Level.WARNING, msg);
				break;
			case Executor.ERROR:
				logger.log(Level.SEVERE, msg);
				break;
		}
	}

	@Override
	protected void log(byte level, String TAG, String msg, Throwable t) {
		Logger logger = Logger.getLogger(TAG);
		switch(level) {
			case Executor.TRACE:
				logger.log(Level.FINER, msg, t);
				break;
			case Executor.DEBUG:
				logger.log(Level.FINE, msg, t);
				break;
			case Executor.INFO:
				logger.log(Level.INFO, msg, t);
				break;
			case Executor.WARN:
				logger.log(Level.WARNING, msg, t);
				break;
			case Executor.ERROR:
				logger.log(Level.SEVERE, msg, t);
				break;
		}
	}

	@Override
	public void log(byte level, String msg) {
	}


}
