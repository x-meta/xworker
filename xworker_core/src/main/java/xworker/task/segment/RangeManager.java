package xworker.task.segment;

/**
 * 段管理器接口。
 * 
 * @author Administrator
 *
 */
public interface RangeManager {
	/** 获取下一个分段 */
	public Range getNextRange();
	
	/** 一个分段结束了 */
	public void rangeFinished(Range range);
	
	/** 一个分段失败了 */
	public void rangeFailed(Range range);
}
