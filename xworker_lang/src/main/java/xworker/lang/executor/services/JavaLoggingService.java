package xworker.lang.executor.services;

import xworker.lang.executor.Executor;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaLoggingService extends AbstractLogService {
	@Override
	protected void log(byte level, String TAG, String msg) {
		Logger logger = Logger.getLogger(TAG);
		logger.log(getLevel(logger, level), msg);
	}

	@Override
	protected void log(byte level, String TAG, String msg, Throwable t) {
		Logger logger = Logger.getLogger(TAG);
		logger.log(getLevel(logger, level), msg, t);
	}

	@Override
	public void log(byte level, String msg) {
		log(level, "", msg);
	}

	private Level getLevel(Logger logger, byte level){
		switch(level) {
			case Executor.TRACE:
				if(logger.isLoggable(Level.FINER)){
					return Level.FINER;
				}else{
					getLevel(logger, (byte) (level + 1));
				}
			case Executor.DEBUG:
				if(logger.isLoggable(Level.FINE)){
					return Level.FINE;
				}else{
					getLevel(logger, (byte) (level + 1));
				}
			case Executor.INFO:
				if(logger.isLoggable(Level.INFO)){
					return Level.INFO;
				}else{
					getLevel(logger, (byte) (level + 1));
				}
			case Executor.WARN:
				if(logger.isLoggable(Level.WARNING)){
					return Level.WARNING;
				}else{
					getLevel(logger, (byte) (level + 1));
				}
			case Executor.ERROR:
				return Level.SEVERE;
		}

		return Level.SEVERE;
	}

}
