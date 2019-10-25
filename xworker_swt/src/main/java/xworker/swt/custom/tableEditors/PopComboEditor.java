package xworker.swt.custom.tableEditors;

import java.util.Map;

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
import org.xmeta.util.UtilString;

import xworker.swt.xworker.AttributeEditor;

@ActionClass(creator="createInstance")
public class PopComboEditor extends AbstractTableEditor{
	Thing attribute;
	Composite composite;
	ActionContainer actions;
	Text text;
	
	@Override
	public Object create(ActionContext actionContext) {
		attribute = self.doAction("getAttribute", actionContext);
		
		//创建popCombo
		Thing popCombo = new Thing("xworker.swt.xworker.PopCombo");
		popCombo.getAttributes().putAll(attribute.getAttributes());
		for(Thing child : attribute.getChilds()){
		    popCombo.addChild(child, false);
		}
		
		String inputattrs = attribute.getString("inputattrs");
		if(inputattrs == null || "".equals(inputattrs)){
		    //没有设置输入扩展属性的
		}else{
		    Map<String, String> params = UtilString.getParams(inputattrs, "&");
		    popCombo.getAttributes().putAll(params);
		    popCombo.set("name", attribute.get("name"));
		}
		
		composite = popCombo.doAction("create", actionContext);       
		text =  (Text) composite.getData(AttributeEditor.INPUT_CONTROL);
		text.addListener(SWT.DefaultSelection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				PopComboEditor.this.saveValue();
				PopComboEditor.this.doDispose();
			}
		});
		//Composite composite = (Composite) text.getData("composite");
		//actions = (ActionContainer) composite.getData(AttributeEditor.ACTIONCONTAINER);
		
		//composite.setData(AttributeEditor.ACTIONCONTAINER, comboAction);
		return composite;      
	}

	@Override
	@ActionParams(names="value")
	public void setValue(Object value, ActionContext actionContext) {
    	if(value == null) {
    		text.setText("");
    	}else {
    		text.setText(String.valueOf(value));
    	}
    	text.selectAll();
    	text.setFocus();     
	}

	@Override
	public Object getValue(ActionContext actionContext) {
		//return actions.doAction("getValue", actionContext);
		return text.getText();
	}

	@Override
	public void doDispose() {
		composite.dispose();
	}

	public static PopComboEditor createInstance(ActionContext actionContext) {
		return ItemEditorUtils.createInstance(PopComboEditor.class, actionContext);
	}

}
