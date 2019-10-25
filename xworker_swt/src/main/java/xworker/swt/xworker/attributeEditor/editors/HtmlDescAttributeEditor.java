package xworker.swt.xworker.attributeEditor.editors;

import java.util.Map;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolItem;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.ThingRegistor;
import org.xmeta.util.UtilString;

import xworker.swt.form.FormModifyListener;
import xworker.swt.xworker.attributeEditor.AttributeEditor;
import xworker.util.XWorkerUtils;

public class HtmlDescAttributeEditor extends AttributeEditor{

	public HtmlDescAttributeEditor(Thing formThing, Thing attribute, xworker.swt.form.GridData gridData, ActionContext actionContext) {
		super(formThing, attribute, gridData, actionContext);
	}

	@Override
	public Control createControl(Composite parent, FormModifyListener modifyListener, Listener defaultSelection) {
		//创建Html说明编辑器数据对象
    	Thing htmlEditorObj = new Thing();
    	htmlEditorObj.set("descriptors", "xworker.swt.xworker.HtmlDescEditor");
    	htmlEditorObj.set("name", inputName);			    	
    	
    	//创建GridData
    	String inputAttrs = attribute.getString("inputattrs"); 
	    Map<String, String> attr = UtilString.getParams(inputAttrs, "&");
	    if(attr.size() == 0 && !"".equals(inputAttrs) && inputAttrs != null){
	    	//兼容以前只设置了事物路径的情况
	    	attr.put("thingPath", inputAttrs);
	    }
	    int hwidth = UtilString.getInt(attr.get("width"));
	    int hh = UtilString.getInt(attr.get("height"));		
	    htmlEditorObj.set("toolbarSet", attr.get("ToolbarSet"));
    	Thing htmlEditorGridData = new Thing();
    	htmlEditorGridData.set("descriptors", ThingRegistor.getPath("_xworker_thing_attribute_editor_GridData"));
    	String style = attr.get("style");
    	if(style == null || "".equals(style)){
    		style = "FILL_BOTH";
    	}
		htmlEditorGridData.set("style", style);
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
        try{
        	Composite comp = htmlEditorObj.doAction("create", context);
        	Thing htmlEditor = (Thing) comp.getData("htmlThing");
        	//重载setValue和getValue
        	htmlEditor.set("descriptors", "xworker.swt.xworker.HtmlDescEditor");
        	
        	ActionContext htmlActionContext = (ActionContext) htmlEditor.get("actionContext");
        	//取消编辑的
        	((ToolItem) htmlActionContext.get("toolItem")).dispose();
        	//设置Browser的地址
        	Browser browser = (Browser) htmlActionContext.get("browser");
        	browser.setUrl(XWorkerUtils.getThingDescUrl(attr.get("thingPath")));
        	
        	return comp;
        }finally{
        	context.pop();
        }
	}

}
