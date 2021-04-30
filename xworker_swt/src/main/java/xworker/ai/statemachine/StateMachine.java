package xworker.ai.statemachine;

/**
 * 状态机的接口。
 * 
 * @author zyx
 *
 */
public interface StateMachine {

	/**
	 * 设置新的状态。
	 * 
	 * @param state
	 */
	public void setState(String state);
	
	/**
	 * 返回当前的状态。
	 * 
	 * @return
	 */
	public State getCurrentState();
	
	public void put(String key , Object value);
	
	public void set(String key, Object value);
	
	public Object get(String key);
	
	public <T> T getObject(String key);
}
