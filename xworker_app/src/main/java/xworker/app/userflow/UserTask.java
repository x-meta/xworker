package xworker.app.userflow;

public class UserTask {
	/** 等待执行 */
	public static final byte WAIT = 0;
	
	/** 正在执行 */
	public static final byte RUNNING = 1;
	
	/**　已结束 */
	public static final byte FINISHED = 2;
	
	/** 被中止 */
	public static final byte CANCELED = 3;
}
