package xworker.dataObject.swt.reacts;

import java.util.HashMap;
import java.util.Map;

public class DataObjectReactorContext {
	/** 调用者的缓存，避免重复递归的调用 */
	private Map<Object, Object> context = new HashMap<Object, Object>();
	private int count = 0; //调用次数
	
	public DataObjectReactorContext() {
	}
	
	public boolean isExists(Object object) {
		if(context.get(object) != null) {
			return true;
		}else{
			context.put(object, object);
			addCount();
			return false;
		}
	}
	
	public synchronized void addCount() {
		count++;
	}
	
	public synchronized void removeCount() {
		count--;
		
		if(count <= 0) {
			DataObjectReactor.setContext(null);
		}
	}
}	
