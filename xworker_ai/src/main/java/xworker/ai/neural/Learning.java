package xworker.ai.neural;

import java.util.Date;

import org.xmeta.Thing;

/**
 * 用于管理神经网络训练的一个接口。
 * 
 * @author Administrator
 *
 */
public interface Learning {
	/**
	 * 返回训练开始的时间。
	 * 
	 * @return
	 */
	public Date getLearningStartTime();
	
	/**
	 * 是否正在训练。
	 * 
	 * @return
	 */
	public boolean isLarning();
	
	/**
	 * 停止训练。
	 */
	public void stopLearning();
	
	/**
	 * 获取神经网络的事物。
	 * 
	 * @return
	 */
	public Thing getNeuralThing();
}
