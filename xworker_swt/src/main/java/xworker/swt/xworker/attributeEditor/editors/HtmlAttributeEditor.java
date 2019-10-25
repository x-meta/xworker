package xworker.swt.xworker.attributeEditor.editors;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.ThingRegistor;
import org.xmeta.util.UtilString;

import xworker.swt.form.FormModifyListener;
import xworker.swt.xworker.attributeEditor.AttributeEditor;

public class HtmlAttributeEditor extends AttributeEditor{

	public HtmlAttributeEditor(Thing formThing, Thing attribute, xworker.swt.form.GridData gridData, ActionContext actionContext) {
		super(formThing, attribute, gridData, actionContext);
	}

	@Override
	public Control createControl(Composite parent, FormModifyListener modifyListener, Listener defaultSelection) {
		//创建HtmlEditor编辑器数据对象
    	Thing htmlEditorObj = new Thing();
    	htmlEditorObj.set("descriptors", ThingRegistor.getPath("_xworker_thing_attribute_editor_HtmlEditor"));
    	htmlEditorObj.set("name", inputName);			    	
    	
    	//创建GridData
    	String inputAttrs = attribute.getString("inputattrs"); 
	    Map<String, String> attr = UtilString.getParams(inputAttrs, ";");
	    int hwidth = UtilString.getInt(attr.get("Width"));
	    int hh = UtilString.getInt(attr.get("Height"));		
	    htmlEditorObj.set("toolbarSet", attr.get("ToolbarSet"));
    	Thing htmlEditorGridData = new Thing();
    	htmlEditorGridData.set("descriptors", ThingRegistor.getPath("_xworker_thing_attribute_editor_GridData"));
    	if(!"false".equals(attr.get("fillBoth"))){
    		htmlEditorGridData.set("style", "FILL_BOTH");
    		htmlEditorGridData.set("height", "80");
    	}
    	htmlEditorGridData.set("name", name + "InputGridData");
    	if(hwidth > 0){
    		htmlEditorGridData.set("width",  "" + hwidth);
    	}
    	if(hh > 0){
    		htmlEditorGridData.set("height", "" + hh);
    	}
    	htmlEditorGridData.set("horizontalSpan", "" + (getColspan(xgridData.colspan)));
    	htmlEditorGridData.set("verticalSpan", "" + (xgridData.rowspan));
    	htmlEditorObj.addChild(htmlEditorGridData);
    	
  	    Bindings bindings = context.push(null);
    	bindings.put("parent", parent);
    	bindings.put("modifyListener", modifyListener);
    	bindings.put("__editThing__", attribute.getParent());//正编辑的事物
        try{
        	Control control = htmlEditorObj.doAction("create", context);
        	return control;
        }finally{
        	context.pop();
        }
	}

	
}
