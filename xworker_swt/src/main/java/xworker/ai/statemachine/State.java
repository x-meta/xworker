package xworker.ai.statemachine;

/**
 * 状态接口。
 * 
 * @author zyx
 *
 */
public interface State {
    public Object enter();
    
    public Object doAction();
    
    public Object exit();
    
    public String getName();
    
    /**
     * 返回状态所属的状态机。
     * 
     * @return
     */
    public StateMachine getStateMachine();
    
	public void put(String key , Object value);
	
	public void set(String key, Object value);
	
	public Object get(String key);
	
	public <T> T getObject(String key);
}
