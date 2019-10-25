package xworker.swt.xworker.attributeEditor.editors;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.util.ThingRegistor;
import org.xmeta.util.UtilString;

import xworker.swt.custom.StyledTextProxy;
import xworker.swt.design.Designer;
import xworker.swt.form.FormModifyListener;
import xworker.swt.xworker.attributeEditor.AttributeEditor;

public class CodeEditorAttributeEditor extends AttributeEditor{

	public CodeEditorAttributeEditor(Thing formThing, Thing attribute, xworker.swt.form.GridData gridData, ActionContext actionContext) {
		super(formThing, attribute, gridData, actionContext);
	}

	@Override
	public Control createControl(Composite parent, FormModifyListener modifyListener, Listener defaultSelection) {
		//创建TextEditor编辑器数据对象
    	Thing codeEditorObj = new Thing();
    	codeEditorObj.set("descriptors", ThingRegistor.getPath("_xworker_thing_attribute_editor_CodeEditor"));
    	codeEditorObj.set("name", inputName);
    	
    	String codeType = "Java";
    	String codeName = "Java";
    	String inputAttris = attribute.getString("inputattrs");
    	Map<String, String> paras = UtilString.getParams(inputAttris, " ");
    	if(paras.get("codeType") != null){
    		codeType = paras.get("codeType");
    	}
    	if(paras.get("codeName") != null){
    		codeName = paras.get("codeName");
    	}
    	if(paras.get("wrap") != null){
    		codeEditorObj.set("WRAP", paras.get("wrap"));
    	}
    	codeEditorObj.set("codeName", codeName);
    	codeEditorObj.set("codeType", codeType);
    	
    	//创建GridData
        int hwidth = UtilString.getInt(paras.get("cols"));
        int hh = UtilString.getInt(paras.get("rows"));
        Thing codeEditorGridData = new Thing();
        if(!"false".equals(paras.get("fillBoth"))){
        	codeEditorGridData.set("style", "FILL_BOTH");
        	codeEditorGridData.set("height", "80");
        }			        
        codeEditorGridData.set("descriptors", ThingRegistor.getPath("_xworker_thing_attribute_editor_GridData"));
        if(hwidth > 0){
        	codeEditorGridData.set("width",  "" + hwidth);
        }
        if(hh > 0){
        	codeEditorGridData.set("height", "" + hh);
        }
        codeEditorGridData.set("horizontalSpan", "" + (getColspan(xgridData.colspan)));
        codeEditorGridData.set("verticalSpan", "" + (xgridData.rowspan));			    	
    	codeEditorObj.addChild(codeEditorGridData);
		    
		    Bindings bindings = context.push(null);
    	bindings.put("parent", parent);
        try{
        	Control obj = codeEditorObj.doAction("create", context);		
        	if(obj instanceof Text) {
        		Text text = (Text) obj;
        		if(modifyListener != null ){
        			text.addModifyListener(modifyListener);
        		}
    		}else if(StyledTextProxy.isStyledText(obj)) {
    			if(modifyListener != null ){
    				StyledTextProxy.addModifyListener(obj, modifyListener);
    	        }
    		}
        	addDefaultSelection(obj, defaultSelection);
        	
        	
        	initCodeAssistor(obj, context);
        	Designer.attach((Control) obj, path, context, true);
        	
        	return obj;
        }finally{
        	context.pop();
        }		
	}

}
