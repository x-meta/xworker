package xworker.swt.xworker.attributeEditor.editors;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import xworker.swt.design.Designer;
import xworker.swt.form.FormModifyListener;
import xworker.swt.form.ThingDescriptorForm;
import xworker.swt.xworker.attributeEditor.AttributeEditor;

public class CheckBoxAttributeEditor extends AttributeEditor{

	public CheckBoxAttributeEditor(Thing formThing, Thing attribute, xworker.swt.form.GridData gridData, ActionContext actionContext) {
		super(formThing, attribute, gridData, actionContext);
	}

	@Override
	public Control createControl(Composite parent, FormModifyListener modifyListener, Listener defaultSelection) {
		int radioCompositeStyle = SWT.NONE;
        Composite radioComposite =  new Composite(parent, radioCompositeStyle);
        Designer.attach(radioComposite, path, context, true);
        
        GridLayout radioCompositeFillLayout = new GridLayout();
        radioCompositeFillLayout.numColumns = 4;
        radioComposite.setLayout(radioCompositeFillLayout);
        
        GridData radioCompositeGridData = new GridData();
        radioCompositeGridData.horizontalSpan = getColspan(xgridData.colspan);
        radioCompositeGridData.verticalSpan = xgridData.rowspan;
        radioComposite.setLayoutData(radioCompositeGridData);

        String inputAttrs = attribute.getString("inputattrs");
        Thing dataStoreThing = ThingDescriptorForm.getDataStoreThing(attribute);
        Thing dataStore = null;
        ActionContext ac = null;
    	if(dataStoreThing != null){
    		//DataStore的变量上下文
    		ac = new ActionContext();
    		ac.put("parent", radioComposite);
    		ac.put("parentContext", context);
    		ac.put("attribute", attribute);
    	    ac.put("composite", radioComposite);
    		ac.put("modifyListener", modifyListener);
    		ac.put("params", context.get("params"));
    		ac.put("selectType", "checkBox");
    		dataStore = (Thing) dataStoreThing.doAction("create", ac);		
    		context.put(name + "Store", dataStore);
    	}
        if(inputAttrs != null){
        	String params[] = inputAttrs.split("[,]");
        	//可见列数
        	if(params.length > 1 && !"".equals(params[1])){
        		try{
        			radioCompositeFillLayout.numColumns = Integer.parseInt(params[1]);
        		}catch(Exception e){			        			
        		}
        	}
//        	
//        	//绑定的控件
//        	if(params.length > 2 && dataStore != null){
//        		String bindTo = params[2];
//        		String bindType = null;
//        		if(params.length > 3){
//        			bindType = params[3];
//        		}
//        		if(bindType == null || "".equals(bindType)){
//        			bindType = "modify";
//        		}			        		
//        		if(bindTo != null && !"".equals(bindTo)){
//        			AttributeEditorBind bind = new AttributeEditorBind(dataStore, bindTo, bindType, ac);
//        			binds.add(bind);
//        		}
//        	}			        	
        }

        if(dataStore == null){ //如果数据仓库为空走原来的流程		
	        // 设置选择列表
	        List<Thing> values = getAttributeValues(attribute, context);			        

	        int index = -1;
	        if(values != null){
		        for(int k=0; k<values.size(); k++){
		        	Thing v = values.get(k);
		            index ++;
		            Button radioButton1 = new Button(radioComposite, SWT.CHECK);
		            //radioButton1.setFont(Resources.getFont("default"));
		            radioButton1.setData(v.getString("value"));
		            String label = v.getString("label");
		            if(label != null){
		                radioButton1.setText(label);
		            }else{
		                radioButton1.setText(v.getString("value"));
		            }			     
		            if(modifyListener != null){
		            	radioButton1.addSelectionListener(modifyListener.getSelectionListener());
			        }
		            
		            addDefaultSelection(radioButton1, defaultSelection);		            
		        }
		        index = 0;
	        }
        }else{
        	context.getScope(0).put(name + "DataStore", dataStore);
        }
        
        context.getScope(0).put(inputName, radioComposite);
        
        return radioComposite;
	}

}
