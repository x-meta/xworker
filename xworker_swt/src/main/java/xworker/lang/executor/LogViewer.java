package xworker.lang.executor;

import org.eclipse.swt.widgets.Display;

public interface LogViewer {
	public void log(byte level, String msg);
	
	public boolean isAutoScroll();
	
	public void setAutoScroll(boolean autoScroll);
	
	public void print(Object message);
	public void println(Object message);
	public void errPrint(Object message);
	public void errPrintln(Object message);
	
	public Display getDisplay();
}
