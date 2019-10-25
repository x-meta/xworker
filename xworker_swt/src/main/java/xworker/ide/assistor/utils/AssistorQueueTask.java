package xworker.ide.assistor.utils;

/**
 * 辅助对了的任务。
 * 
 * @author Administrator
 *
 */
public interface AssistorQueueTask {
	/** 
	 * 执行任务。
	 */
	public void doTask();
	
	/**
	 * 任务执行完后，如果可以通知辅助者，那么会调用此方法通知。
	 */
	public void doNotify();
	
	/**
	 * 设置已执行，如果在doTask之前接受到此命令，那么执行 doTask时应该什么也不做。
	 */
	public void setExecuted();
}
