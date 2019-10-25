package xworker.lang.system;

/**
 * 进程管理服务的接口定义。
 * 
 * @author zyx
 *
 */
public interface IProcessManager {
	/**
	 * 添加一个进程到进程管理器。
	 * 
	 * @param name
	 * @param process
	 */
	public void addProcess(String name, Process process);
	
	/**
	 * 进程管理器是否已经销毁。
	 * 
	 * @return
	 */
	public boolean isDisposed();
}
