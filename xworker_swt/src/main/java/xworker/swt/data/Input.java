package xworker.swt.data;

/**
 * 　用于替换Model的设计更合理的框架，但也有可能会抛弃。
 * 
 * @author zhangyuxiang
 *
 */
public interface Input {
	/**
	 * 向输入控件设置值。
	 * 
	 * @param value
	 */
	public void setValue(Object value);
	
	/**
	 * 从输入控件取值。
	 * 
	 * @return
	 */
	public Object getValue();
	
	/**
	 * 校验输入，如果校验失败返回提示字符串，否则返回null。
	 * 
	 * @return
	 */
	public String validate();
	
	/**
	 * 向输入控件设置一个提示。
	 * 
	 * @param message
	 */
	public void markTip(String message);
	
	/**
	 * 移除标记的提示。
	 * 
	 */
	public void removeMarkTip();
}
