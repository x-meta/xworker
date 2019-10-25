package xworker.swt.xwidgets.clock;

public interface ClockTask {
	public boolean isFinished(Clock clock);
	
	public void run(Clock clock);
	
	public long getInterval(Clock clock);
}
