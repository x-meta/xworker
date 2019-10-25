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

import xworker.swt.design.Designer;
import xworker.swt.form.FormModifyListener;
import xworker.swt.form.ThingDescriptorForm;
import xworker.swt.xworker.attributeEditor.AttributeEditor;

public class SelectAttributeEditor extends AttributeEditor{

	public SelectAttributeEditor(Thing formThing, Thing attribute, xworker.swt.form.GridData gridData, ActionContext actionContext) {
		super(formThing, attribute, gridData, actionContext);
	}

	@Override
	public Control createControl(Composite parent, FormModifyListener modifyListener, Listener defaultSelection) {
		int comboComboStyle = SWT.DROP_DOWN | SWT.BORDER;
        comboComboStyle |= SWT.READ_ONLY;
        CCombo comboCombo = new CCombo(parent, comboComboStyle);
        comboCombo.setVisibleItemCount(15);
        comboCombo.setData("attribute", attribute);
        Designer.attach(comboCombo, path, context, true);
        
        Thing dataStoreThing = ThingDescriptorForm.getDataStoreThing(attribute);
        Thing dataStore = null;
        ActionContext ac = null;
        if(dataStoreThing != null){
    		//DataStore的变量上下文
    		ac = new ActionContext(); 
    		ac.setLabel("select");
    		ac.put("parent", comboCombo);
    		ac.put("parentContext", context);
    		ac.put("attribute", attribute);
    		ac.put("combo", comboCombo);
    		ac.put("thingAttributes", attribute);
    		ac.put("modifyListener", modifyListener);
    		ac.put("params", context.get("params"));
    		dataStore = (Thing) dataStoreThing.doAction("create", ac);		
    		context.put(name + "Store", dataStore);
    	}
        
        String inputAttrs = attribute.getString("inputattrs");
        if(inputAttrs != null){
        	String params[] = inputAttrs.split("[,]");
        	//可见列数
        	if(params.length > 1 && !"".equals(params[1])){
        		try{
        			comboCombo.setVisibleItemCount(Integer.parseInt(params[1]));
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
        }
        
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
        
        if(dataStore != null){
        	//如果使用DataStore，那么返回事物型的输入
        	Thing model = new Thing("xworker.swt.Widgets/@actions");
        	model.set("extends", "xworker.app.view.swt.widgets.form.AttributeSelectModel/@actions");
        	model.put("swtControl", name + "ComboInput");
        	model.put("attribute", attribute);
        	ac.put(name + "ComboInput", comboCombo);
        	ac.put("self", model);
        	context.getScope(0).put(inputName, model.doAction("create", ac));
        	context.getScope(0).put(name + "ComboInput", comboCombo);
        	context.getScope(0).put(name + "SwtInput", comboCombo);
        	context.getScope(0).put(name + "DataStore", dataStore);
        }else{
        	context.getScope(0).put(inputName, comboCombo);
        }
        
        return comboCombo;
	}

}
