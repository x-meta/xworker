package xworker.swt.xworker.attributeEditor.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import xworker.swt.design.Designer;
import xworker.swt.form.FormModifyListener;
import xworker.swt.xworker.attributeEditor.AttributeEditor;

public class TruefalseselectAttributeEditor extends AttributeEditor{

	public TruefalseselectAttributeEditor(Thing formThing, Thing attribute, xworker.swt.form.GridData gridData, ActionContext actionContext) {
		super(formThing, attribute, gridData, actionContext);
	}

	@Override
	public Control createControl(Composite parent, FormModifyListener modifyListener, Listener defaultSelection) {
		int comboComboStyle = SWT.DROP_DOWN | SWT.BORDER;
        comboComboStyle |= SWT.READ_ONLY;
        CCombo comboCombo = new CCombo(parent, comboComboStyle);
        comboCombo.setVisibleItemCount(3);
        comboCombo.setData("attribute", attribute);
        Designer.attach(comboCombo, path, context, true);
        
        // 设置选择列表
        Thing yesNo = World.getInstance().getThing("xworker.lang.attributes.YesNo");			        
        List<Thing> values = yesNo.getChilds();
        	//(List<Thing>) f.getAttribute("_xwork_values");
        //value = dataObject.getString(f.metadata.name);
        List<String> datas = new ArrayList<String>();
        datas.add("");
        if(values != null){
	        for(int k=0; k<values.size(); k++){
	        	Thing v = values.get(k);
	        	datas.add(v.getString("value"));
	        }
        }
        
        comboCombo.setData(datas);
        int index = -1;
        int selectIndex = -1;
        comboCombo.add("");
        if(values != null){
	        for(int k=0; k<values.size(); k++){
	        	Thing v = values.get(k);
	            index ++;
	            if(v.getString("label") != null){
	                comboCombo.add(v.getMetadata().getLabel());
	            }else{
	                comboCombo.add(v.getMetadata().getLabel());
	            }			            
	        }
        }
        comboCombo.select(selectIndex + 1);
        
        GridData comboComboGridData = new GridData();
        // println f.name + f.getInt("colspan", 1);
        comboComboGridData.horizontalSpan = getColspan(xgridData.colspan);
        comboComboGridData.verticalSpan = xgridData.rowspan;
        comboComboGridData.widthHint = _width * attribute.getInt("size", 25);
        comboCombo.setLayoutData(comboComboGridData);			        
        
        if(modifyListener != null){
        	comboCombo.addModifyListener(modifyListener);
        }
        addDefaultSelection(comboCombo, defaultSelection);
        
    	context.getScope(0).put(inputName, comboCombo);
    	
    	return comboCombo;
	}
}
