package xworker.util.autoplay;

public interface AutoPlayNode {
	/** 返回延迟执行的时间 */
	public long getDelay();
	
	/**
	 * 执行节点。
	 *  
	 * @param step 是否是单步执行
	 */
	public void run(boolean step);
	
	/**
	 * 返回节点所属的父节点。
	 * 
	 * @return
	 */
	public AutoPlayNodeGroup getParnet();
}
