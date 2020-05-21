package xworker.lang.updator;

public interface AttributeSetter {
	/**
	 * 设置对象的属性的值。
	 * 
	 * @param obj 对象
	 * @param name 属性名
	 * @param value 属性的值
	 */
	public void setAttribute(Object obj , String name,  Object value) throws Exception;
}
