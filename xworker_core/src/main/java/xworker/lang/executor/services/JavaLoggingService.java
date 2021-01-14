package xworker.lang.executor.services;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import xworker.lang.executor.ExecuteRequest;
import xworker.lang.executor.Executor;

public class JavaLoggingService extends AbstractLogService {
	Logger logger = Logger.getLogger("XWorker");
	
	@Override
	public List<ExecuteRequest> getRequestUIs() {
		return null;
	}

	@Override
	public Thread getThread() {
		return null;
	}

	@Override
	public void log(byte level, String msg) {
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
}
