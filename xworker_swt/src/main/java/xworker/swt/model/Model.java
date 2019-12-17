package xworker.swt.model;

/**
 * 对于新的属性编辑器类型可以使用Model，并通过ModelManager注册，从而实现取值和设置值的方法。
 * 
 * @author zhangyuxiang
 *
 */
public interface Model {
	/**
	 * 
	 * @param control
	 * @param value
	 */
	public void setValue(Object control, Object value, String viewPattern, String editPattern);
	
	public Object getValue(Object control, String type, String pattern);
}
