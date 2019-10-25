package xworker.task;

public interface UserTaskListener {
	/** 已经启动 */
	public void started(UserTask task);
	
	/** 已经结束 */
	public void finished(UserTask task);
	
	/** 已经设置进度 */
	public void progressSetted(UserTask task, int progress);
	
	/** 当前任务标签已更新 */
	public void currentLabelUpdated(UserTask task, String label);
}
