package xworker.app.view.swt.widgets.form;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Control;
import org.xmeta.Action;
import org.xmeta.ActionContext;
import org.xmeta.Bindings;
import org.xmeta.Thing;

import xworker.dataObject.DataObject;
import xworker.lang.executor.Executor;

public class AttributeSelectModel {
	private static final String TAG = AttributeSelectModel.class.getName();
	
	public static void setValue(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Thing thingAttributes = actionContext.getObject("thingAttributes");
		
		String name = self.getMetadata().getName();
		Object value = actionContext.get("value");
		if(value == null && thingAttributes != null){
		    value = thingAttributes.get((String) name);
		}

		Control combo = (Control) actionContext.get(self.getString("swtControl"));
		if(combo != null){
		    //println combo.getData("storeListener");
			Thing storeListener = (Thing) combo.getData("storeListener");
			storeListener.doAction("setSelection", actionContext, "id", value);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Object getValue(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		Object comboObj = actionContext.get(self.getString("swtControl"));
		if(!(comboObj instanceof CCombo)){
			CCombo combo = (CCombo) comboObj;
		    //Debuger.debug(actionContext);
		    List<Bindings> scopes = actionContext.getScopes();
		    Executor.info(TAG, "comboclas=" + combo.getClass() + ",swtControl=" + self.getMetadata().getPath());
		    
		    for(int i=scopes.size() - 1; i>=0; i--){
		        Bindings bindings = scopes.get(i);
		        //log.info("cls" + i + "=" + bindings.caller);
		        if(bindings.getCaller() instanceof Action){
		            //log.info("action=" + aciton.thing.metadata.path);
		        }
		    }
		    return null;
		}

		CCombo combo = (CCombo) comboObj;
		Thing attribute = actionContext.getObject("attribute");
		int index = combo.getSelectionIndex();
		if(index >= 0 && actionContext.get("attribute") != null && attribute.getBoolean("optional")){
		    //log.info("optional=" + attribute.optional);
		    index = index - 1;
		}

		//log.info("getComboValue,index=" + index);
		if(index == -1){
		    if(combo instanceof CCombo && combo.getEditable()){
		        return combo.getText();
		    }else{
		        return null;
		    }
		}else{
		    Thing store = (Thing) combo.getData("store");
		    List<DataObject> records = (List<DataObject>) store.get("records");
		    if(records.size() > index){
		        DataObject record = records.get(index);
		        
		        //按照属性所映射的值来取
		        attribute = (Thing) self.get("attribute");
		        //println(attribute);
		        String relationValueField = attribute.getStringBlankAsNull("relationValueField");
		        if(relationValueField != null){
		            return record.get(relationValueField);
		        }
		        
		        //按照数据对象的默认值来取
		        Thing dataObject = (Thing) store.get("dataObject");
		        String valueName = dataObject.getStringBlankAsNull("valueName");
		        if(valueName != null){
		            return record.get(valueName);
		        }
		        
		        Object[][] keys = record.getKeyAndDatas();
		        if(keys != null && keys.length > 0){
		            return keys[0][1];
		        }else{
		            Executor.warn(TAG, "record has no key, record=" + record + ", dataObject=" + record.getMetadata().getDescriptor());
		        }
		    }
		    
		    return null;
		}
	}
	
	public static Object getControl(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		return actionContext.get(self.getString("swtControl"));
	}
	
	public static Object getRootControl(ActionContext actionContext) {
		Thing self = actionContext.getObject("self");
		return actionContext.get(self.getString("swtControl"));
	}
}
