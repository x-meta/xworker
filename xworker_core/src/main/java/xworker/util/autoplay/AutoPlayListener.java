package xworker.util.autoplay;

/**
 * 用于监听AutoPlay的状态。
 * 
 * @author zhangyuxiang
 *
 */
public interface AutoPlayListener {
	/**
	 * 一个节点已经执行完了。
	 * 
	 * @param node
	 */
	public void executed(AutoPlay autoPaly, AutoPlayNode node, boolean success);
	
	/**
	 * 准备要执行的一个节点了。
	 * 
	 * @param node
	 */
	public void beforeExecute(AutoPlay autoPaly, AutoPlayNode node);
}
