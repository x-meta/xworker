package xworker.swt.custom.tableEditors;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.annotation.ActionClass;
import org.xmeta.annotation.ActionParams;

@ActionClass(creator="createInstance")
public class CComboEditor extends AbstractTableEditor{
	CCombo ccombo;
	List<Thing> values;
	
	@Override
	public Object create(ActionContext actionContext) {
		values = self.doAction("getValues", actionContext);
		
		Thing comboThing = null;
		if(self.getBoolean("readOnly")) {
			comboThing = World.getInstance().getThing("xworker.swt.custom.tableEditors.CComboEditor/@combo1");
		}else {
			comboThing = World.getInstance().getThing("xworker.swt.custom.tableEditors.CComboEditor/@combo");
		}
		
		ccombo = comboThing.doAction("create", actionContext);
		//添加一个空行
		Thing blank = new Thing("xworker.lang.MetaDescriptor3/@attribute/@value");
		blank.set("label", " ");
		blank.set("value", "");
		
		values.add(0, blank);
		ccombo.setData("thing", self);
		if(values != null) {
			for(Thing value : values) {
				ccombo.add(value.getMetadata().getLabel());
			}
		}
		ccombo.addListener(SWT.DefaultSelection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				CComboEditor.this.saveValue();
				CComboEditor.this.doDispose();
			}
			
		});
		
		ccombo.forceFocus();
		return ccombo;
	}

	@Override
	@ActionParams(names="value")
	public void setValue(Object value, ActionContext actionContext) {
		if(value == null) {
			ccombo.setText("");
			return;
		}
		
		String svalue = String.valueOf(value);
		if(values != null) {
			for(Thing valueThing : values) {
				if(svalue.equals(valueThing.getString("value"))) {
					ccombo.setText(valueThing.getMetadata().getLabel());
					return;
				}
			}
		}
		
		ccombo.setText(svalue);
		ccombo.forceFocus();
	}

	@Override
	public Object getValue(ActionContext actionContext) {
		int index = ccombo.getSelectionIndex();
		if(index != -1) {
			return values.get(index).get("value");
		}else {
			return ccombo.getText();
		}
	}

	@Override
	public void doDispose() {
		ccombo.dispose();
	}

	public static CComboEditor createInstance(ActionContext actionContext) {
		return ItemEditorUtils.createInstance(CComboEditor.class, actionContext);
	}
}
