package xworker.swt.model;

public interface Model {
	/**
	 * 
	 * @param control
	 * @param value
	 */
	public void setValue(Object control, Object value, String viewPattern, String editPattern);
	
	public Object getValue(Object control, String type, String pattern);
}
