package xworker.swt.util;

/**
 * 用于创建Item时传入索引位置。
 * 
 * @author zyx
 *
 */
public class ItemIndex {
	private static ThreadLocal<Integer> indexLocal = new ThreadLocal<Integer>();
	
	public static void remove() {
		indexLocal.remove();
	}
	
	public static void set(int index) {
		indexLocal.set(index);
	}
	
	/**
	 * 返回ThreadLocal设置的index，同时remove。
	 * 
	 * @return
	 */
	public static Integer getRemove() {
		Integer index =  indexLocal.get();
		indexLocal.remove();
		return index;
	}
}
