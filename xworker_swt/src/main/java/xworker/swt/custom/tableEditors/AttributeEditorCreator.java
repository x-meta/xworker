/*******************************************************************************
* Copyright 2007-2013 See AUTHORS file.
 * 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package xworker.swt.custom.tableEditors;

import java.util.List;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionParams;

import xworker.swt.form.ThingDescriptorForm;

/**
 * 表格属性编辑器。
 * 
 * @author Administrator
 *
 */
@ActionClass(creator="createInstance")
public class AttributeEditorCreator extends AbstractTableEditor{
	Thing attribute;
	String inputType = null;
	Thing editorThing;
	
    public  Object create(ActionContext actionContext){
    	World world = World.getInstance();
    	attribute = self.doAction("getAttribute", actionContext);
    	if(attribute != null) {
    		inputType = attribute.getStringBlankAsNull("inputtype");
    	}
    	actionContext.peek().put("attribute", attribute);
    	if("openWindow".equals(inputType)) {
    		//打开窗口
    		editorThing = new Thing("xworker.swt.custom.tableEditors.PopWindowEditor");
    		editorThing.set("inputattrs", getAttribute("inputattrs"));
    	}else if("text".equals(inputType) || inputType == null) {
    		//文本框
    		editorThing = new Thing("xworker.swt.custom.tableEditors.TextEditor");
    	}else if("truefalse".equals(inputType)){
    		//truefalse
    		editorThing = new Thing("xworker.swt.custom.tableEditors.CheckBoxEditor");
    		editorThing.set("trueText", "true");
    		editorThing.set("falseText", "false");
    	}else if("datePick".equals(inputType)){
    		//datePick
    		editorThing = new Thing("xworker.swt.custom.tableEditors.DatePickerEditor");
    		editorThing.set("pattern", getAttribute("pattern"));
    	}else if("dateTime".equals(inputType)){
    		//dateTime
    		editorThing = new Thing("xworker.swt.custom.tableEditors.DatePickerEditor");
    		editorThing.set("pattern", getAttribute("pattern"));
    		editorThing.set("showTime", "true");
    	}else if("truefalse".equals(inputType)){
    		//truefalse
    		editorThing = new Thing("xworker.swt.custom.tableEditors.CheckBoxEditor");
    		editorThing.set("trueText", "true");
    		editorThing.set("falseText", "false");
    	}else if("select".equals(inputType) || "inputSelect".equals(inputType)){
    		//下拉框或者输入下拉框
    		if(attribute != null) {
    			Thing dataStore = ThingDescriptorForm.getDataStoreThing(attribute);
    			if(dataStore != null) {
    				actionContext.g().put("dataStore", dataStore);
    				editorThing = new Thing("xworker.swt.custom.tableEditors.CComboStoreEditor");
    				editorThing.set("dataStore", "var:dataStore");
    				if("select".equals(inputType)) {
    					editorThing.set("readOnly", "true");
    				}else {
    					editorThing.set("readOnly", "false");
    				}
    			}else {
    				List<Thing> values = attribute.getAllChilds("value");
    				actionContext.g().put("_values_", values);
    				editorThing = new Thing("xworker.swt.custom.tableEditors.CComboEditor");
    				editorThing.set("values", "var:_values_");
    				if("select".equals(inputType)) {
    					editorThing.set("readOnly", "true");
    				}else {
    					editorThing.set("readOnly", "false");
    				}
    			}
    		}else {
    			editorThing = new Thing("xworker.swt.custom.tableEditors.CComboStringEditor");
    		}    		
    	}else if("multSelect".equals(inputType)){
    		//multSelect
    		editorThing = new Thing("xworker.swt.custom.tableEditors.MultiSelectComboEditor");
    		editorThing.set("attributeThing", "var:attribute");
    	}else if("treeSelector".equals(inputType)){
    		//treeSelector
    		editorThing = new Thing("xworker.swt.custom.tableEditors.TreeComboEditor");
    		editorThing.set("attributeThing", "var:attribute");
    	}else if("popCombo".equals(inputType)){
    		//popCombo
    		editorThing = new Thing("xworker.swt.custom.tableEditors.PopComboEditor");
    		editorThing.set("attributeThing", "var:attribute");
    	}else if("truefalseselect".equals(inputType)){
    		//truefalseselect
    		Thing yesNo = world.getThing("xworker.lang.attributes.YesNo");
    		List<Thing> values = yesNo.getChilds("value");
			actionContext.g().put("_values_", values);
			editorThing = new Thing("xworker.swt.custom.tableEditors.CComboEditor");
			editorThing.set("values", "var:_values_");
			editorThing.set("readOnly", "true");
    	}else if("password".equals(inputType)){
    		//password
    		editorThing = new Thing("xworker.swt.custom.tableEditors.TextEditor");
    		editorThing.set("PASSWORD", "true");
    	}else if("time".equals(inputType)){
    		//time
    		editorThing = new Thing("xworker.swt.custom.tableEditors.TimeEditor");
    		if(attribute != null) {
    			editorThing.set("pattern", attribute.get("pattern"));
    		}
    	}else if("dataSelector".equals(inputType)){
    		//dataSelector
    		editorThing = new Thing("xworker.swt.custom.tableEditors.DialogEditor");
    		editorThing.set("shell", "xworker.ide.worldexplorer.swt.tools.ThingSelector/@shell");
    	}else if("pathSelector".equals(inputType)){
    		//pathSelector
    		editorThing = new Thing("xworker.swt.custom.tableEditors.DialogEditor");
    		editorThing.set("shell", "xworker.ide.worldexplorer.swt.tools.ThingSelector/@shell");
    		actionContext.g().put("selectType", "category");
    	}else if("fontSelect".equals(inputType)){
    		//fontSelect
    		editorThing = new Thing("xworker.swt.custom.tableEditors.FontEditor");    		
    	}else if("colorpicker".equals(inputType)){
    		//colorpicker
    		editorThing = new Thing("xworker.swt.custom.tableEditors.ColorEditor");    		
    	}else if("imageSelector".equals(inputType)){
    		//imageSelector
    		editorThing = new Thing("xworker.swt.custom.tableEditors.DialogEditor");
    		editorThing.set("shell", "xworker.swt.xworker.attributeEditor.dialogs.ImageSelector");
    	}else if("file".equals(inputType)){
    		//pathSelector
    		editorThing = new Thing("xworker.swt.custom.tableEditors.FileDialogEditor");
    		if(attribute != null) {
    			editorThing.set("inputattrs", attribute.get("inputattrs"));
    		}    		
    	}else if("filePath".equals(inputType)){
    		//filePath
    		editorThing = new Thing("xworker.swt.custom.tableEditors.DirectoryEditor");
    		if(attribute != null) {
    			editorThing.set("inputattrs", attribute.get("inputattrs"));
    		}   
    	}else if("progressBar".equals(inputType)) { 
    		editorThing = new Thing("xworker.swt.custom.tableEditors.ProgressBarEditor");
    		if(attribute != null) {
    			editorThing.set("inputattrs", attribute.get("inputattrs"));
    		}   
    	}else if("selfDefined".equals(inputType)){
    		editorThing = new Thing("xworker.swt.custom.tableEditors.CustomEditor");
    		if(attribute != null) {
    			editorThing.set("inputattrs", attribute.get("inputattrs"));
    		}   
    	} else {
			//使用默认
    		editorThing = new Thing("xworker.swt.custom.tableEditors.AttributeCommonEditor");
    		editorThing.set("attribute", "var:attribute");    		
    	}
    	
    	return editorThing.doAction("create", actionContext);
    }
    
    private Object getAttribute(String key) {
    	if(attribute != null) {
    		return attribute.get(key);
    	}
    	
    	return null;
    }

	public  Object getValue(ActionContext actionContext){
    	return editorThing.doAction("getValue", actionContext);
	}

	@Override
	@ActionParams(names="value")
	public void setValue(Object value, ActionContext actionContext) {
		//value参数在actionContext已经有了
		editorThing.doAction("setValue", actionContext);
	}

	@Override
	public void doDispose() {
			
	}
	
	public static AttributeEditorCreator createInstance(ActionContext actionContext) {
		String key = "key_AttributeEditorCreator";
		AttributeEditorCreator editor = actionContext.getObject(key);
		if(editor == null) {
			editor = new AttributeEditorCreator();
			actionContext.g().put(key, editor);
		}
		
		return editor;
	}

}