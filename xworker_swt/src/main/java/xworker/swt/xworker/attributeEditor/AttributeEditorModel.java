package xworker.swt.xworker.attributeEditor;

import xworker.swt.model.Model;

public class AttributeEditorModel implements Model{

	@Override
	public void setValue(Object control, Object value, String viewPattern, String editPattern) {
		AttributeEditor editor = (AttributeEditor) control;
		editor.setValue(value, viewPattern, editPattern);
	}

	@Override
	public Object getValue(Object control, String type, String pattern) {
		AttributeEditor editor = (AttributeEditor) control;
		return editor.getValue();
	}

}
