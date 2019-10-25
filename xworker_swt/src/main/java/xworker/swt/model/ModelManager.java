package xworker.swt.model;

import java.util.HashMap;
import java.util.Map;

public class ModelManager {
	/** 各种Model可以注册到这里 */
	private static Map<Class<?>, Model> models = new HashMap<Class<?>, Model>();
	
	public static void regist(Class<?> cls, Model model) {
		models.put(cls, model);
	}
	
	public static Model getModel(Class<?> cls) {
		return models.get(cls);
	}
}
