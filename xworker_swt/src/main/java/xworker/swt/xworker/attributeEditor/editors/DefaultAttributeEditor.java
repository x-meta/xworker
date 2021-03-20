package xworker.swt.xworker.attributeEditor.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.lang.executor.Executor;
import xworker.swt.custom.StyledTextProxy;
import xworker.swt.design.Designer;
import xworker.swt.form.FormModifyListener;
import xworker.swt.xworker.EditorConfig;
import xworker.swt.xworker.attributeEditor.AttributeEditor;

public class DefaultAttributeEditor extends AttributeEditor{
	private static final String TAG = DefaultAttributeEditor.class.getName();

	public DefaultAttributeEditor(Thing formThing, Thing attribute, xworker.swt.form.GridData gridData, ActionContext actionContext) {
		super(formThing, attribute, gridData, actionContext);
	}

	@Override
	public Control createControl(Composite parent, FormModifyListener modifyListener, Listener defaultSelection) {
		//配置的编辑控件，按照以下优先顺序查找
    	Thing swtEditor = null;
    	String inputType = attribute.getString("inputtype");
    	
    	//查看是否有自定义的输入方式
		swtEditor = (Thing) attribute.get("SwtObject@0/inputEditor@0");
    	
    	//从注册输入控件表中查询
		if(swtEditor == null){
			swtEditor = EditorConfig.getAttributeEditor(inputType);
			/*
	    	Thing editorConfig = (Thing) ThingRegistor.get("_xworker_thing_attribute_editor_config");
	    	if(editorConfig != null){
	    		Thing cfg = editorConfig.getThing("@" + inputType);
	    		if(cfg != null){
	    			swtEditor = World.getInstance().getThing(cfg.getString("default"));
	    		}
	    	}*/
		}
    	
    	//是否通过扩展属性自定
    	if(swtEditor == null){
    		if("inputAttrDefined".equals(inputType)){
				swtEditor = World.getInstance().getThing(attribute.getString("inputattrs"));
			}
    	}
    	
    	//创建编辑控件
	    if(swtEditor != null){
	    	try{
	    		GridData gridData = new GridData();
	    		gridData.horizontalSpan = getColspan(xgridData.colspan);
	    		gridData.verticalSpan = xgridData.rowspan;
	    		gridData.widthHint = _width * attribute.getInt("size", 25);
		        
	    		Bindings bindings = context.push(null);
	    		bindings.put("parent", parent);
	    		bindings.put("gridData", xgridData);
	    		bindings.put("layoutData", gridData);
	    		bindings.put("attribute", attribute);			
	    		bindings.put("isThingEditor", true);
	    		bindings.put("modifyListener", modifyListener);
	    		bindings.put("defaultSelection", defaultSelection);
	    		
	    		Control control = swtEditor.doAction("create", context);
	    		if(control == null){
	    			Executor.warn(TAG, "Created self defined attribute editor control is null, path=" + swtEditor.getMetadata().getPath());
	    		}else {
	    			if(control.getLayoutData() == null) {
	    				control.setLayoutData(gridData);
	    			}
	    			Object object = context.getObject(inputName);
		    		Control editControl = object instanceof Control ? (Control) object : null;
		    		if(control != null){			
		    			org.xmeta.util.ActionContainer actionContainer = xworker.swt.xworker.AttributeEditor.getActionContainer(control);
		    			if(actionContainer != null){
		    				editControl = actionContainer.doAction("getControl");
		    			}else{
		    				editControl = xworker.swt.xworker.AttributeEditor.getInputControl(control);
		    				if(editControl == null){
		    					editControl = control;
		    				}				    				
		    			}
		    			
		    			if(context.g().get(inputName) == null) {
		    				context.getScope(0).put(inputName, editControl);
		    			}
		    			if(editControl instanceof Text){
							this.initCodeAssistor(editControl, context);
						}
		    		}
		    		Designer.attach(editControl, path, context, true);
		    		
		    		if(editControl != null && modifyListener != null){
		    			if(editControl instanceof Combo){
		    				((Combo) editControl).addModifyListener(modifyListener);
		    			}else if(editControl instanceof Spinner){
		    				((Spinner) editControl).addModifyListener(modifyListener);
		    			}else if(editControl instanceof Text){
		    				((Text) editControl).addModifyListener(modifyListener);
		    			}else if(editControl instanceof CCombo){
		    				((CCombo) editControl).addModifyListener(modifyListener);
		    			}else if(StyledTextProxy.isStyledText(editControl)) {
		        			StyledTextProxy.addModifyListener(editControl, modifyListener);
		        		}
		    		}
		    		
		    		addDefaultSelection(editControl, defaultSelection);
		    		
		    		return control;
	    		}
	    	}finally{
	    		context.pop();
	    	}	     	
	    	
	    	return null;
	    }else{
	        // 默认
	        int textTextStyle = SWT.SINGLE | SWT.LEFT;
	        textTextStyle |= SWT.BORDER;
	        if("password".equals(inputType)){
	        	textTextStyle |= SWT.PASSWORD;
	        }
	        Text textText = new Text(parent, textTextStyle);
	        Designer.attach(textText, path, context, true);
	        //textText.setFont(Resources.getFont("default"));			   

	        GridData textTextGridData = new GridData();
	        textTextGridData.widthHint = _width * attribute.getInt("size", 25);
	        textTextGridData.horizontalSpan = getColspan(xgridData.colspan);
	        textTextGridData.verticalSpan = xgridData.rowspan;
	        textText.setLayoutData(textTextGridData);
	        
	        if(modifyListener != null){
	        	textText.addModifyListener(modifyListener);
	        }
	        if(defaultSelection != null) {
	        	textText.addListener(SWT.DefaultSelection, defaultSelection);
	        }
	        
	        initCodeAssistor(textText, context);
	        context.getScope(0).put(inputName, textText);	
	        
	        return textText;
    	}
	}

}
