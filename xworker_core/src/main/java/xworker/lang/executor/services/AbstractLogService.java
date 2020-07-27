package xworker.lang.executor.services;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.spi.LocationInfo;
import org.slf4j.helpers.MessageFormatter;

import xworker.lang.executor.ExecuteRequest;
import xworker.lang.executor.Executor;
import xworker.lang.executor.ExecutorService;

public abstract class AbstractLogService implements ExecutorService{
	
	private final static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public abstract void log(byte level, String msg);
	
	private byte level = Executor.INFO;
	
	@Override
	public void setLogLevel(byte level) {
		this.level = level;
	}
	
	@Override
	public byte getLogLevel() {
		return level;
	}
	
	private void log(byte level, String TAG, String msg) {
		StringBuilder sb = new StringBuilder();
		synchronized(sf) {
			sb.append(sf.format(new Date()));
		}
		
		sb.append(" ");
		sb.append(getLevelName(level));
		sb.append(" (");
		sb.append(TAG);
		//sb.append(":");  //不准确，取消打印行数，比如在Groovy脚本时
		//LocationInfo locationInfo = new LocationInfo(new Throwable(), TAG);
		//sb.append(locationInfo.fullInfo);
		
		sb.append(") ");
		sb.append(msg);
		
		log(level, sb.toString());
	}
	
	private void log(byte level, String TAG, String msg, Throwable t) {
		StringBuilder sb = new StringBuilder();
		synchronized(sf) {
			sb.append(sf.format(new Date()));
		}
		
		sb.append(" ");
		sb.append(getLevelName(level));
		sb.append(" (");
		sb.append(TAG);
        //sb.append(":");  //不准确，取消打印行数，比如在Groovy脚本时
		//LocationInfo locationInfo = new LocationInfo(new Throwable(), TAG);
		//sb.append(locationInfo.getLineNumber());
		sb.append(") ");
		sb.append(msg);
		sb.append("\n");
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		t.printStackTrace(new PrintStream(bout));		
		sb.append(bout.toString());
		
		log(level, sb.toString());
	}
	
	private String getLevelName(byte level) {
		switch(level) {
		case Executor.TRACE:
			return "TRACE";
		case Executor.DEBUG:
			return "DEBUG";
		case Executor.INFO:
			return "INFO";
		case Executor.WARN:
			return "WARN";
		case Executor.ERROR:
			return "ERROR";
		}
		
		return "UNKNOWN";
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
		log(Executor.TRACE, TAG, MessageFormatter.format(format, arg).getMessage());
	}

	@Override
	public void trace(String TAG, String format, Object arg1, Object arg2) {
		log(Executor.TRACE, TAG, MessageFormatter.format(format, arg1, arg2).getMessage());
	}

	@Override
	public void trace(String TAG, String format, Object... arguments) {
		log(Executor.TRACE, TAG, MessageFormatter.arrayFormat(format, arguments).getMessage());
		
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
		log(Executor.DEBUG, TAG, MessageFormatter.format(format, arg).getMessage());
	}

	@Override
	public void debug(String TAG, String format, Object arg1, Object arg2) {
		log(Executor.DEBUG, TAG, MessageFormatter.format(format, arg1, arg2).getMessage());
	}

	@Override
	public void debug(String TAG, String format, Object... arguments) {
		log(Executor.DEBUG, TAG, MessageFormatter.arrayFormat(format, arguments).getMessage());		
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
		log(Executor.INFO, TAG, MessageFormatter.format(format, arg).getMessage());
	}

	@Override
	public void info(String TAG, String format, Object arg1, Object arg2) {
		log(Executor.INFO, TAG, MessageFormatter.format(format, arg1, arg2).getMessage());
	}

	@Override
	public void info(String TAG, String format, Object... arguments) {
		log(Executor.INFO, TAG, MessageFormatter.arrayFormat(format, arguments).getMessage());		
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
		log(Executor.WARN, TAG, MessageFormatter.format(format, arg).getMessage());
	}

	@Override
	public void warn(String TAG, String format, Object arg1, Object arg2) {
		log(Executor.WARN, TAG, MessageFormatter.format(format, arg1, arg2).getMessage());
	}

	@Override
	public void warn(String TAG, String format, Object... arguments) {
		log(Executor.WARN, TAG, MessageFormatter.arrayFormat(format, arguments).getMessage());		
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
		log(Executor.ERROR, TAG, MessageFormatter.format(format, arg).getMessage());
	}

	@Override
	public void error(String TAG, String format, Object arg1, Object arg2) {
		log(Executor.ERROR, TAG, MessageFormatter.format(format, arg1, arg2).getMessage());
	}

	@Override
	public void error(String TAG, String format, Object... arguments) {
		log(Executor.ERROR, TAG, MessageFormatter.arrayFormat(format, arguments).getMessage());		
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
	public void removeRequest(ExecuteRequest request) {
		Executor.superRemoveRequest(this, request);
	}

	@Override
	public void requestUI(ExecuteRequest request) {
		Executor.superRequestUI(this, request);
	}
	
	
}
