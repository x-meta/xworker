package xworker.swt.reacts;

import java.util.HashMap;
import java.util.Map;

public class DataReactorContext {
	/** 调用者的缓存，避免重复递归的调用 */
	//private Map<Object, Object> context = new HashMap<Object, Object>();
	
	public DataReactorContext() {
	}
	
	public boolean isExists(Object object) {
		return false;
		/*
		System.out.println("isExists: " + object);

		if(context.get(object) != null) {
			return true;
		}else{
			context.put(object, object);
			return false;
		}*/
	}
}	
