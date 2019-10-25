package xworker.swt.custom.tableEditors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionParams;
import org.xmeta.util.ActionContainer;

import xworker.swt.form.ThingDescriptorForm;
import xworker.swt.xworker.AttributeEditor;
import xworker.swt.xworker.attributeEditor.MultiSelectComboEditorCreator;

@ActionClass(creator="createInstance")
public class MultiSelectComboEditor extends AbstractTableEditor{
	Thing attribute;
	Composite composite;
	ActionContainer actions;
	Text text;
	
	@SuppressWarnings("unchecked")
	@Override
	public Object create(ActionContext actionContext) {
		attribute = self.doAction("getAttribute", actionContext);
		
		//为不污染原始动作上下文，新建动作上下文
		ActionContext context = new ActionContext();		
		context.put("parentContext", actionContext);		
		context.put("attribute", attribute);
		
		//输入编辑器
		Thing inputThing = new Thing("xworker.swt.xworker.MultiSelectCombo");
		inputThing.put("BORDER", "true");
		inputThing.put("READONLY", self.getString("readOnly"));
		String inputAttrs = attribute.getString("inputattrs");        
        if(inputAttrs != null && !"".equals(inputAttrs)){
        	String params[] = inputAttrs.split("[,]");
        	if(params.length > 1){
        		inputThing.put("popWinMaxRows", params[1]);
        	}
        	if(params.length > 2){
        		inputThing.put("filter", params[2]);
        	}
        }else{        
        	inputThing.put("filter", "false");
        }
		
		//数据仓库
	    Thing dataStoreThing = ThingDescriptorForm.getDataStoreThing(attribute);
	    if(dataStoreThing != null){
	    	
	        Thing dataStore = (Thing) dataStoreThing.doAction("create", context);
	        context.put("dataStore", dataStore);
	        inputThing.put("dataSource", "dataStore");
	        inputThing.put("dataName", "dataStore");
	        inputThing.put("idName", MultiSelectComboEditorCreator.getIdField(dataStore));
	        inputThing.put("labelName", MultiSelectComboEditorCreator.getLabelField(dataStore));
	    }else{
		    //自定义数据
		    inputThing.put("dataSource", "selfValues");
		    inputThing.put("idName", "value");		    
		    for(Thing value : (java.util.List<Thing>) attribute.get("value@")){
		        inputThing.addChild(value, false);
		    }
		}
		
	    context.put("parent", actionContext.get("parent"));
		composite = (Composite) inputThing.doAction("create", context);
		text =  (Text) composite.getData(AttributeEditor.INPUT_CONTROL);
		text.addListener(SWT.DefaultSelection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				MultiSelectComboEditor.this.saveValue();
				MultiSelectComboEditor.this.doDispose();
			}
		});
		//Composite composite = (Composite) text.getData("composite");
		actions = (ActionContainer) composite.getData(AttributeEditor.ACTIONCONTAINER);
		
		//composite.setData(AttributeEditor.ACTIONCONTAINER, comboAction);
		return composite;      
	}

	@Override
	@ActionParams(names="value")
	public void setValue(Object value, ActionContext actionContext) {
		actions.doAction("setValue", actionContext, "value", value);
		text.forceFocus();
	}

	@Override
	public Object getValue(ActionContext actionContext) {
		return actions.doAction("getValue", actionContext);
	}

	@Override
	public void doDispose() {
		composite.dispose();
	}

	public static MultiSelectComboEditor createInstance(ActionContext actionContext) {
		return ItemEditorUtils.createInstance(MultiSelectComboEditor.class, actionContext);
	}
}
