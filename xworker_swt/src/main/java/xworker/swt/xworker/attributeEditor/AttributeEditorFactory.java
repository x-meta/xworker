package xworker.swt.xworker.attributeEditor;

import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.xworker.attributeEditor.editors.CheckBoxAttributeEditor;
import xworker.swt.xworker.attributeEditor.editors.CodeEditorAttributeEditor;
import xworker.swt.xworker.attributeEditor.editors.ColorpickerAttributeEditor;
import xworker.swt.xworker.attributeEditor.editors.DataSelectorAttributeEditor;
import xworker.swt.xworker.attributeEditor.editors.DefaultAttributeEditor;
import xworker.swt.xworker.attributeEditor.editors.FontSelectAttributeEditor;
import xworker.swt.xworker.attributeEditor.editors.HtmlAttributeEditor;
import xworker.swt.xworker.attributeEditor.editors.HtmlDescAttributeEditor;
import xworker.swt.xworker.attributeEditor.editors.ImageAttributeEditor;
import xworker.swt.xworker.attributeEditor.editors.InputSelectAttributeEditor;
import xworker.swt.xworker.attributeEditor.editors.PathSelectorAttributeEditor;
import xworker.swt.xworker.attributeEditor.editors.RadioAttributeEditor;
import xworker.swt.xworker.attributeEditor.editors.SelectAttributeEditor;
import xworker.swt.xworker.attributeEditor.editors.TextAttributeEditor;
import xworker.swt.xworker.attributeEditor.editors.TextareaAttributeEditor;
import xworker.swt.xworker.attributeEditor.editors.TruefalseAttributeEditor;
import xworker.swt.xworker.attributeEditor.editors.TruefalseselectAttributeEditor;

public class AttributeEditorFactory {
	public static AttributeEditor createAttributeEditor(Thing formThing, Thing attribute, xworker.swt.form.GridData gridData, ActionContext actionContext) {
		String inputType = attribute.getString("inputtype");
		if("text".equals(inputType)) {
			return new TextAttributeEditor(formThing, attribute, gridData, actionContext); 
		}else if("textarea".equals(inputType)) {
			return new TextareaAttributeEditor(formThing, attribute, gridData, actionContext); 
		}else if("codeEditor".equals(inputType)) {
			return new CodeEditorAttributeEditor(formThing, attribute, gridData, actionContext);
		}else if("html".equals(inputType)) {
			return new HtmlAttributeEditor(formThing, attribute, gridData, actionContext);
		}else if("htmlDesc".equals(inputType)) {
			return new HtmlDescAttributeEditor(formThing, attribute, gridData, actionContext);
		}else if("select".equals(inputType)) {
			return new SelectAttributeEditor(formThing, attribute, gridData, actionContext);
		}else if("truefalse".equals(inputType)) {
			return new TruefalseAttributeEditor(formThing, attribute, gridData, actionContext);
		}else if("truefalseselect".equals(inputType)) {
			return new TruefalseselectAttributeEditor(formThing, attribute, gridData, actionContext);
		}else if("radio".equals(inputType)) {
			return new RadioAttributeEditor(formThing, attribute, gridData, actionContext);
		}else if("checkBox".equals(inputType)) {
			return new CheckBoxAttributeEditor(formThing, attribute, gridData, actionContext);
		}else if("dataSelector".equals(inputType)) {
			return new DataSelectorAttributeEditor(formThing, attribute, gridData, actionContext);
		}else if("pathSelector".equals(inputType)) {
			return new PathSelectorAttributeEditor(formThing, attribute, gridData, actionContext);
		}else if("inputSelect".equals(inputType)) {
			return new InputSelectAttributeEditor(formThing, attribute, gridData, actionContext);
		}else if("colorpicker".equals(inputType)) {
			return new ColorpickerAttributeEditor(formThing, attribute, gridData, actionContext);
		}else if("fontSelect".equals(inputType)) {
			return new FontSelectAttributeEditor(formThing, attribute, gridData, actionContext);
		}else if("image".equals(inputType)){
			return new ImageAttributeEditor(formThing, attribute, gridData, actionContext);
		}else {
			return new DefaultAttributeEditor(formThing, attribute, gridData, actionContext);
		}
	}
}
