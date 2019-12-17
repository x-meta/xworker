package xworker.swt.model;

import java.util.HashMap;
import java.util.Map;

import xworker.swt.xworker.attributeEditor.AttributeEditorModel;
import xworker.swt.xworker.attributeEditor.editors.ImageAttributeEditor;

public class ModelManager {
	/** 各种Model可以注册到这里 */
	private static Map<Class<?>, Model> models = new HashMap<Class<?>, Model>();
	
	static {
		regist(ImageAttributeEditor.class, new AttributeEditorModel());
	}
	
	public static void regist(Class<?> cls, Model model) {
		models.put(cls, model);
	}
	
	public static Model getModel(Class<?> cls) {
		return models.get(cls);
	}
}
